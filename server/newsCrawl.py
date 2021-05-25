from bs4 import BeautifulSoup
import threading
import requests
import re
import datetime
from pymongo import MongoClient
import pandas as pd
import logging

class newsCrawl(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.INTERVAL = 60
        self.logger = self.__get_logger()

        data = pd.read_csv('company_code.txt', dtype=str, sep='\t')  # 종목코드 추출
        company_name = data['회사명']
        keys = [i for i in company_name]  # 데이터프레임에서 리스트로 바꾸기
        company_code = data['종목코드']
        values = [j for j in company_code]

        self.dict_result = dict(zip(keys, values))  # 딕셔너리 형태로 회사이름과 종목코드 묶기

    def __get_logger(self):
        __logger = logging.getLogger('logger')
        formatter = logging.Formatter('%(levelname)s##%(asctime)s : %(message)s')
        stream_handler = logging.StreamHandler()
        stream_handler.setFormatter(formatter)
        __logger.addHandler(stream_handler)
        __logger.setLevel(logging.INFO)
        return __logger

    def crawler(self, company, companyCode, date):
        page = 1
        flag = True
        while flag:
            url = 'https://finance.naver.com/news/news_search.nhn?q=' + str(
                companyCode) + '&x=0&y=0&sm=all.basic&pd=4&stDateStart=' + date + '&stDateEnd=' + date + '&page=' + str(
                page)
            source_code = requests.get(url).text
            html = BeautifulSoup(source_code, "lxml")
            titles = [p for p in html.find_all(class_='articleSubject')]
            if len(titles) == 0:
                break

            title_result = []
            for title in titles:
                title = title.get_text().strip()
                title = re.sub('\n', '', title)
                title = re.sub('\t', '', title)
                title = re.sub('\[', '<', title)
                title = re.sub(']', '>', title)
                title_result.append(title)

            # 뉴스 링크
            links = [a for a in html.find_all(class_='articleSubject')]
            link_result = []
            for link in links:
                add = 'https://finance.naver.com' + link.find('a')['href']
                link_result.append(add)

            # 뉴스 날짜
            dates = html.select('.wdate')
            date_result = [date.get_text().strip() for date in dates]
            time_result = []
            for i in range(len(date_result)):
                date_result[i] = re.sub('\t', '', date_result[i])
                date_result[i] = re.sub('\n', '', date_result[i])
                time_result.append(date_result[i][-5:])
                date_result[i] = date_result[i][:-5]

            # 뉴스 매체
            sources = html.select('.press')
            source_result = [source.get_text().strip() for source in sources]

            for i in range(len(title_result)):
                client = MongoClient(
                    "mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
                if client.Project.newsData.find_one(
                        {"회사명": company, "날짜": date_result[i], "언론사": source_result[i], "기사제목": title_result[i]}) is not None:
                    flag = False
                    break
                client.Project.newsData.insert_one(
                    {"회사명": company, "날짜": date_result[i], "시간": time_result[i],
                     "언론사": source_result[i], "기사제목": title_result[i], "링크": link_result[i]})
                client.close()

            page += 1

    def crawl_set_period(self, year_i, month_i, date_i, year_o, month_o, date_o):

        start = datetime.date(year_i, month_i, date_i)
        end = datetime.date(year_o, month_o, date_o)

        dates = []
        tmp = start
        while True:
            dates.append(tmp)
            tmp = tmp + datetime.timedelta(days=1)
            if tmp >= end:
                break

        for d in dates:
            for com in self.dict_result:
                self.logger.info("start {} {}'s news downloading...".format(d, com))
                self.crawler(com, self.dict_result[com], d)
                self.logger.info("done!")

    def run(self, interval=60):
        self.logger.info("crawling real time news...")
        date = datetime.datetime.now().strftime("%Y-%m-%d")

        for com in self.dict_result:
            self.logger.info("start {} {}'s news downloading...".format(date, com))
            self.crawler(com, self.dict_result[com], date)
            self.logger.info("done!")

        threading.Timer(interval, self.run).start()

