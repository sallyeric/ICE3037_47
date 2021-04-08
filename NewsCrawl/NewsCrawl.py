from bs4 import BeautifulSoup
import requests
import re
import pandas as pd
import os

from google.colab import drive
drive.mount('/content/gdrive')

# 경로 지정
os.chdir('/content/gdrive/My Drive/Colab Notebooks/')


# def crawler(company_code, maxpage):
def crawler(company, companyCode, startDate, endDate, maxpage):
    page = 1 
    
    while page <= int(maxpage):
#         url = 'https://finance.naver.com/item/news_news.nhn?code=' + str(company_code) +'&page=' + str(page) 
        url = 'https://finance.naver.com/news/news_search.nhn?q='+str(companyCode)+'&x=0&y=0&sm=all.basic&pd=4&stDateStart=' + str(startDate) + '&stDateEnd=' + str(endDate) + '&page=' + str(page)
        source_code = requests.get(url).text
        html = BeautifulSoup(source_code, "lxml")

        # #회사명
        # company_result=[]
        # for title in titles:
        #   company_result.append(company)

        # 회사명 & 뉴스 제목
#         titles =  html.find_all('.title')
        
        titles = [p for p in html.find_all(class_='articleSubject')]
        title_result=[]
        company_result=[]
        for title in titles:
            title = title.get_text() 
            title = re.sub('\n','',title)
            title = re.sub('\t','',title)
            title_result.append(title)
            company_result.append(company)
            
        
        # 뉴스 링크
        links = [a for a in html.find_all(class_='articleSubject')]
        link_result =[]
        for link in links: 
            add = 'https://finance.naver.com' + link.find('a')['href']
            link_result.append(add)

        # 뉴스 날짜 
        dates = html.select('.wdate')
        date_result = [date.get_text() for date in dates]
        for i in range(len(date_result)):
            date_result[i] = re.sub('\t','',date_result[i])
            date_result[i] = re.sub('\n','',date_result[i])
        
        # 뉴스 매체
        sources = html.select('.press')
        source_result = [source.get_text() for source in sources]
        
        # 뉴스 본문
        links = [a for a in html.find_all(class_='articleSubject')]
        content_result =[]
        for link2 in links:
            add2 = 'https://finance.naver.com' + link2.find('a')['href']
            url2 = add2
            
            source_code2 = requests.get(url2).text
            html2 = BeautifulSoup(source_code2, "lxml")
            contents = html2.select('.boardView')
            content_whole = [content.get_text() for content in contents] 
            for i in range(len(content_whole)):
                content_whole[i] = re.sub('\t','',content_whole[i])
                content_whole[i] = re.sub('\n','',content_whole[i])
        
            content_result.append(content_whole)

        # 회사 영문명
        company_eng_name = convert_to_eng(company)
 
        # 변수들 합쳐서 해당 디렉토리에 csv파일로 저장하기 
 
        result= {"회사명" : company_result, "날짜" : date_result, "언론사" : source_result, "기사제목" : title_result, "링크" : link_result ,"본문":content_result} 
        df_result = pd.DataFrame(result)
        
        print("다운 받고 있습니다------")
        df_result.to_csv(str(startDate) + 'page' + str(page) + str(company_eng_name) + '.csv', mode='a', encoding='utf-8-sig') 
            
        page += 1
    print('끝')

# 회사 영문명으로 변경
def convert_to_eng(company):
    data = pd.read_csv('company_eng.txt', dtype=str, sep='\t') 
    company_kor = data['회사명']
    keys = [i for i in company_kor]    #데이터프레임에서 리스트로 바꾸기 
 
    company_eng = data['영문명']
    values = [j for j in company_eng]
 
    dict_result = dict(zip(keys, values))  # 딕셔너리 형태로 회사이름과 종목코드 묶기 
    
    pattern = '[a-zA-Z가-힣]+' 
    
    if bool(re.match(pattern, company)) == True:         # Input에 이름으로 넣었을 때  
        company_eng = dict_result.get(str(company))
        return company_eng
                             
# 종목 리스트 파일 열기  
# 회사명을 종목코드로 변환 

def convert_to_code(company, startDate, endDate, maxpage):
    
    data = pd.read_csv('company_code.txt', dtype=str, sep='\t')   # 종목코드 추출 
    company_name = data['회사명']
    keys = [i for i in company_name]    #데이터프레임에서 리스트로 바꾸기 
 
    company_code = data['종목코드']
    values = [j for j in company_code]
 
    dict_result = dict(zip(keys, values))  # 딕셔너리 형태로 회사이름과 종목코드 묶기 
    
    pattern = '[a-zA-Z가-힣]+' 
    
    if bool(re.match(pattern, company)) == True:         # Input에 이름으로 넣었을 때  
        company_code = dict_result.get(str(company))
        crawler(company, company_code,startDate, endDate, maxpage)
 
    
    # else:                                                # Input에 종목코드로 넣었을 때       
    #     company_code = str(company)      
    #     crawler(company_code, startDate, endDate, maxpage)


def main():
    info_main = input("="*50+"\n"+"실시간 뉴스기사 다운받기."+"\n"+" 시작하시려면 Enter를 눌러주세요."+"\n"+"="*50)
    
    company = input("회사명: ")
    
    startDate = input("시작날짜: ")

    endDate = input("마지막날짜: ")
    
    maxpage = input("최대 뉴스 페이지 수 입력: ")

    convert_to_code(company, startDate, endDate, maxpage)

    #crawler(startDate, endDate, maxpage)
 
    # convert_to_code(company, maxpage)

main()
