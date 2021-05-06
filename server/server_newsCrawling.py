from bs4 import BeautifulSoup
import requests
import re
import datetime
from pymongo import MongoClient
import pandas as pd
import time
import logging

INTERVAL = 60

def __get_logger():
    __logger = logging.getLogger('logger')
    formatter = logging.Formatter('%(levelname)s##%(asctime)s : %(message)s')
    stream_handler = logging.StreamHandler()
    stream_handler.setFormatter(formatter)
    __logger.addHandler(stream_handler)
    __logger.setLevel(logging.INFO)
    return __logger

def crawler(company, companyCode):
    today = datetime.datetime.now().strftime("%Y-%m-%d")
    page = 1
    flag = True
    while flag:
        url = 'https://finance.naver.com/news/news_search.nhn?q=' + str(companyCode) + '&x=0&y=0&sm=all.basic&pd=4&stDateStart=' + str(today) + '&stDateEnd=' + str(today) + '&page=' + str(page)
        source_code = requests.get(url).text
        html = BeautifulSoup(source_code, "lxml")
        titles = [p for p in html.find_all(class_='articleSubject')]
        if len(titles) == 0:
            break

        title_result = []
        company_result = []
        for title in titles:
            title = title.get_text()
            title = re.sub('\n', '', title)
            title = re.sub('\t', '', title)
            title_result.append(title)
            company_result.append(company)

        # 뉴스 링크
        links = [a for a in html.find_all(class_='articleSubject')]
        link_result = []
        for link in links:
            add = 'https://finance.naver.com' + link.find('a')['href']
            link_result.append(add)

        # 뉴스 날짜
        dates = html.select('.wdate')
        date_result = [date.get_text() for date in dates]
        for i in range(len(date_result)):
            date_result[i] = re.sub('\t', '', date_result[i])
            date_result[i] = re.sub('\n', '', date_result[i])

        # 뉴스 매체
        sources = html.select('.press')
        source_result = [source.get_text() for source in sources]

        # 뉴스 본문
        links = [a for a in html.find_all(class_='articleSubject')]
        content_result = []
        for link2 in links:
            add2 = 'https://finance.naver.com' + link2.find('a')['href']
            url2 = add2
            # time.sleep(1)
            source_code2 = requests.get(url2).text
            html2 = BeautifulSoup(source_code2, "lxml")
            contents = html2.select('.boardView')
            content_whole = [content.get_text() for content in contents]
            for i in range(len(content_whole)):
                content_whole[i] = re.sub('\t', '', content_whole[i])
                content_whole[i] = re.sub('\n', '', content_whole[i])

            content_whole = "".join(content_whole).split("원해요0댓글")[1].split("@")[0]
            content_result.append(content_whole)

        for i in range(len(title_result)):
            client = MongoClient("mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
            if client.Project.newsData.find_one({"날짜": date_result[i], "언론사": source_result[i], "기사제목": title_result[i]}) is not None:
                flag = False
                break
            client.Project.newsData.insert_one({"회사명": company_result[i], "날짜": date_result[i], "언론사": source_result[i],
                                                "기사제목": title_result[i], "링크": link_result[i], "본문": content_result[i]})
            client.close()

        page += 1


def crawling():
    data = pd.read_csv('company_code.txt', dtype=str, sep='\t')  # 종목코드 추출
    company_name = data['회사명']
    keys = [i for i in company_name]  # 데이터프레임에서 리스트로 바꾸기

    company_code = data['종목코드']
    values = [j for j in company_code]

    dict_result = dict(zip(keys, values))  # 딕셔너리 형태로 회사이름과 종목코드 묶기

    for com in dict_result:
        logger.info("start {}'s news downloading...".format(com))
        crawler(com, dict_result[com])
        logger.info("done!")

def main():
    while True:
        crawling()

        logger.info("waiting for {}seconds...".format(INTERVAL))
        time.sleep(INTERVAL)
        logger.info("awake!")


logger = __get_logger()
main()