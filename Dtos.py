from pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime

class Item(BaseModel):
    # content: List[str]
    user_input: str
    # password: str
    # email: EmailStr
    # full_name: Optional[str] = None


# 히스토리 모델
class HistoryBase(BaseModel):
    user_no: Optional[int] = None
    user_email: Optional[str] = None
    user_name: Optional[str] = None
    user_nick: Optional[str] = None
    history_content: Optional[str] = None
    history_content_lang: Optional[str] = None
    history_content_result: Optional[str] = None
    history_content_result_lang: Optional[str] = None
    history_create_date: Optional[datetime] = None
    # user_no: int = None
    # user_email: str = None
    # user_name: str = None
    # user_nick: str = None
    # history_content: str = None

class HistoryCreate(HistoryBase):
    pass

class HistoryUpdate(HistoryBase):
    pass

class HistoryOrm(HistoryBase):
    history_no: int = None
    # history_create_date: datetime = None

    class Config:
        orm_mode = True


# 사용자 모델 피덴틱모델
class UserBase(BaseModel):
    user_email: str
    user_name: str
    user_nick: str
    user_pwd: str
    user_image: str = None

class UserCreate(UserBase):
    pass

class UserOrm(UserBase):
    user_no: int
    user_create_date: datetime

    class Config:
        orm_mode = True

class UserLogin(BaseModel):
    user_email: str
    user_pwd: str