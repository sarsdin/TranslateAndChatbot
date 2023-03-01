from sqlalchemy.orm import relationship, sessionmaker, Session
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine, Column, Integer, String, Text, DateTime, ForeignKey, desc

# SQLALCHEMY_DATABASE_URL = "sqlite:///./myapi.db"
SQLALCHEMY_DATABASE_URL = "mysql+pymysql://jm:jm@mysql:3306/tapp_db?charset=utf8mb4"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, encoding="utf-8"  # , connect_args={"check_same_thread": False}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()


class Question(Base):
    __tablename__ = "question"

    id = Column(Integer, primary_key=True)
    subject = Column(String(20), nullable=False)
    content = Column(Text, nullable=False)
    create_date = Column(DateTime, nullable=False)


class Answer(Base):
    __tablename__ = "answer"

    id = Column(Integer, primary_key=True)
    content = Column(Text, nullable=False)
    create_date = Column(DateTime, nullable=False)
    question_id = Column(Integer, ForeignKey("question.id"))
    question = relationship("Question", backref="answers")


class User(Base):
    __tablename__ = 'User'

    user_no = Column(Integer, primary_key=True, autoincrement=True, comment='사용자 번호')
    user_phone = Column(String(50), nullable=True, comment='사용자 휴대폰번호')
    user_email = Column(String(50), nullable=True, comment='사용자 이메일(ID)')
    user_name = Column(String(50), nullable=True, comment='사용자 이름')
    user_nick = Column(String(50), nullable=True, comment='사용자 닉네임')
    user_pwd = Column(String(50), nullable=True, comment='사용자 비밀번호')
    user_create_date = Column(DateTime, nullable=True, comment='가입날짜')
    user_image = Column(String(500), nullable=True, comment='사용자 이미지')


class History(Base):
    __tablename__ = 'History'

    history_no = Column(Integer, primary_key=True, autoincrement=True, comment='히스토리 번호')
    user_no = Column(Integer, ForeignKey("User.user_no"), nullable=True, comment='사용자 번호')
    user_email = Column(String(50), nullable=True, comment='사용자 이메일(ID)')
    user_name = Column(String(50), nullable=True, comment='사용자 이름')
    history_content = Column(Text, nullable=True, comment='히스토리 내용')
    history_content_result = Column(Text, nullable=True, comment='히스토리 번역 결과')
    history_create_date = Column(DateTime, nullable=True, comment='히스토리 생성시간')
    favorite_no = Column(Integer, nullable=True, default=0, comment='히스토리 즐겨찾기유무')
    history_content_lang = Column(String(50), nullable=True, comment='히스토리 내용 언어')
    history_content_result_lang = Column(String(50), nullable=True, comment='히스토리 번역 언어')


class Favorite(Base):
    __tablename__ = 'Favorite'

    favorite_no = Column(Integer, primary_key=True, autoincrement=True, comment='즐겨찾기 번호')
    user_no = Column(Integer, ForeignKey("User.user_no"), nullable=True, comment='사용자 번호')
    user_email = Column(String(50), nullable=True, comment='사용자 이메일(ID)')
    user_name = Column(String(50), nullable=True, comment='사용자 이름')
    favorite_content = Column(Text, nullable=True, comment='즐겨찾기 내용')
    favorite_content_result = Column(Text, nullable=True, comment='즐겨찾기 번역 결과')
    favorite_create_date = Column(DateTime, nullable=True, comment='즐겨찾기 생성시간')
    history_no = Column(Integer, nullable=True, comment='히스토리 번호')
    favorite_content_lang = Column(String(50), nullable=True, comment='즐겨찾기 내용 언어')
    favorite_content_result_lang = Column(String(50), nullable=True, comment='즐겨찾기 번역 언어')


class EmailNumber(Base):
    __tablename__ = 'EmailNumber'

    user_email = Column(String(50), primary_key=True, comment='사용자 이메일(ID)')
    user_name = Column(String(50), nullable=True, comment='사용자 이름')
    vnum = Column(String(50), nullable=True, comment='인증 번호')
    expire_time = Column(DateTime, nullable=True, comment='만료시간')

