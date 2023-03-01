import smtplib
import Util
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

import configparser

config = configparser.ConfigParser()
config.read('config.ini')

# SMTP 서버 정보
smtp_info = {
    'gmail.com': ('smtp.gmail.com', 587),
    'naver.com': ('smtp.naver.com', 587),
    'daum.net': ('smtp.daum.net', 465),
    'hanmail.net': ('smtp.daum.net', 465),
    'nate.com': ('smtp.mail.nate.com', 465),
    'outlook.com': ('smtp.outlook.com', 587),
    }


def send_email(receiver_email: str, code: str):
    # 이메일 발신자, 수신자, 제목, 본문 내용 설정
    sender_email = 'sjeys14@gmail.com'
    # receiver_email = 'recipient_email@gmail.com'
    subject = 'TRANSLATE 새 비밀번호 설정 인증 번호'
    body = f"새 비밀번호 설정을 위한 인증번호입니다.\n인증번호: {code}"

    # 이메일 인증 정보 설정
    password = config['data']['password']
    print(f"send_email password: {password}")
    smtp_server = 'smtp.gmail.com'
    smtp_port = 587  # tsl 전용포트번호

    # 이메일 메시지 생성
    message = MIMEMultipart()
    message['From'] = sender_email
    message['To'] = receiver_email
    message['Subject'] = subject
    message.attach(MIMEText(body, 'plain'))

    # SMTP 서버에 연결 및 이메일 발송
    with smtplib.SMTP(smtp_server, smtp_port) as server:
        server.starttls()
        server.login(sender_email, password)
        text = message.as_string()
        server.sendmail(sender_email, receiver_email, text)

    return {"message": "Email sent successfully."}
