# from main import gpt 에러남
# from main import app 에러남
# from main import Union 에러남
# from pydantic import BaseModel, EmailStr

# import의 datetime이 비활성화로 보이는 것은 밑의 from Dtos import * 안에 이미 datetime 이라는 개체가 선언되어 있기 때문
# 즉, 그것이 선택되어서이다.
from datetime import datetime, timedelta
import Util
import GoogleSmtp

# * 를 사용하면 해당 모듈의 모든 내용(import된 것도 모두 포함하여) 가져온다.
# import된 것도 '변수명 그대로' 가져오기때문에 이름 충돌 문제가 많아진다. 에러가 많아짐..
# 그래서 *를 사용해서 가져올때는 해당 모듈안에 import 된 이름 포함 모든 함수명 클래스명 변수명을 고려하여 중복안되게끔 사용해야한다.
# 그냥 되도록이면 사용하지 말자.
from Dtos import *
# import models
from database import *
from fastapi import APIRouter, Request, HTTPException, Query, Depends
# from src.models.user import UserModel
from typing import Optional, Union, List
import json

# APIRouter creates path operations for any module
# These is End-Point!!
router = APIRouter(
    # prefix="/user",
    # tags=["User"],
    responses={404: {"description": "Not found"}},
)


# Dependency for getting a database session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@router.post("/joinComplete")  # , response_model=UserOrm
def create_user(user: UserCreate):
    db = SessionLocal()
    db_user = User(user_email=user.user_email, user_pwd=user.user_pwd, user_nick=user.user_nick,
                   user_phone=user.user_phone,
                   user_name=user.user_name, user_image=user.user_image, user_create_date=datetime.now())
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return {"userinfo": db_user, "res": "true"}


@router.get("/joinEmailVerify/{user_email}")
def redundant_check(user_email: str):  # , db: Session = Depends(get_db)
    db = SessionLocal()
    exists = db.query(User).filter(User.user_email == user_email).exists()
    query = db.query(exists)
    # query = db.query(exists().where(User.name == user_email))
    # 쿼리를 실행합니다.
    result = query.scalar()
    print("why???????????", query)
    # if not exists:
    #     raise HTTPException(status_code=404, detail="History not found")
    return {"res": result}


@router.get("/isPhoneNumberRedundant/{user_email}")
def redundant_check(user_phone: str, db: Session = Depends(get_db)):
    exists = db.query(User).filter(User.user_phone == user_phone).exists()
    query = db.query(exists)
    # query = db.query(exists().where(User.name == user_email))
    # 쿼리를 실행합니다.
    result = query.scalar()
    print("why???????????", query)
    # if not exists:
    #     raise HTTPException(status_code=404, detail="History not found")
    return {"res": result}


@router.post("/login")
def login(user: UserLogin):
    db = SessionLocal()
    db_user = db.query(User).filter(User.user_email == user.user_email, User.user_pwd == user.user_pwd).first()

    if db_user is None:
        return {"res": "false"}
        # raise HTTPException(status_code=400, detail="Incorrect email or password")

    histories = db.query(History).filter(History.user_no == db_user.user_no).order_by(
        desc(History.history_create_date)).all()
    favorites = db.query(Favorite).filter(Favorite.user_no == db_user.user_no).order_by(
        desc(Favorite.favorite_create_date)).all()
    return {"res": "true", "user_no": db_user.user_no, "user_email": db_user.user_email, "user_nick": db_user.user_nick,
            "histories": histories, "favorites": favorites
            }


@router.post("/findId")
def findId(info: LoginFindId, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.user_phone == info.user_phone, User.user_name == info.user_name).first()

    if db_user is None:
        return {"res": "false"}
        # raise HTTPException(status_code=400, detail="Incorrect email or password")

    return {"res": "true", "user_email": db_user.user_email}


@router.post("/emailVnumReq")
def emailVnumReq(info: LoginFindPw, db: Session = Depends(get_db)):
    # 우선 받아온 email, name 을 이용해서 user table 에 해당 가입자가 있는지 확인함.
    db_user = db.query(User).filter(User.user_name == info.user_name, User.user_email == info.user_email).first()

    if db_user is None:
        return {"res": "false"}
        # raise HTTPException(status_code=400, detail="Incorrect email or password")

    # 일단 난수 생성해서 EmailNumber 테이블에 받은 user_email, user_name, vnum, expire_time 레코드를 생성해야함.
    code = Util.generate_code()
    db_email = EmailNumber(
        user_email=info.user_email,
        user_name=info.user_name,
        vnum=code,
        expire_time=datetime.now() + timedelta(minutes=1, seconds=30)
    )
    db.merge(db_email)
    db.commit()
    # db.refresh(db_email)

    # 구글 이메일 서버로 user_email 주소로 이메일 보내야함..
    GoogleSmtp.send_email(db_user.user_email, code)

    return {"res": "true", "user_email": db_user.user_email}


@router.post("/vnum")
def vnum(info: LoginFindPw, db: Session = Depends(get_db)):
    db_email = db.query(EmailNumber).filter(EmailNumber.vnum == info.vnum, EmailNumber.user_email == info.user_email).first()

    if (db_email is None) or (db_email.expire_time < datetime.now()):
        return {"res": "false"}
        # raise HTTPException(status_code=400, detail="Incorrect email or password")

    return {"res": "true", "user_email": db_email.user_email}



@router.post("/newPasswd")
def vnum(info: LoginNewPw, db: Session = Depends(get_db)):
    update_res = db.query(User).filter(User.user_pwd == info.user_pwd, User.user_email == info.user_email)\
        .update({User.user_pwd: info.user_pwd})
    return {"res": "true", "update_res": update_res}




@router.get("/")
def read_root():
    return {"Hello": "World"}


# @router.get("/items/{item_id}")
# async def read_item(item_id: int, q: Union[str, None] = None):
#     return {"item_id ": item_id, "q": q}


# , response_model=Item, response_model_exclude_unset=True
# 요청이 들어오면 알아서 요청안의 요소중 이름이 같은 요소명을 item class의 이름이 같은 변수로 맵핑하여 인자로 던져줌.
# @router.post("/writeChat")
# async def writeChat(item: Item, req: Request):
#     # past_user_inputs
#     # generated_responses
#     # await req.json()
#     print("hello??")
#     if item.user_input == "--초기화":
#         초기화()
#         return {"past_user_inputs": chat.past_user_inputs, "generated_responses": chat.generated_responses}
#
#     chat.process_req(item.user_input)
#     return {"past_user_inputs": chat.past_user_inputs, "generated_responses": chat.generated_responses}
#
#
# @router.get("/getChat")
# async def getChat():
#     print(chat.past_user_inputs)
#     # generated_responses
#     return {"past_user_inputs": chat.past_user_inputs, "generated_responses": chat.generated_responses}




# CRUD operations for History model
# Create operation
@router.post("/history")  # , response_model=HistoryOrm
def create_history(history: HistoryCreate):  # , db: Session = Depends(get_db)
    db = SessionLocal()
    db_history = History(user_email=history.user_email, user_no=history.user_no,
                         history_content=history.history_content,
                         history_content_lang=history.history_content_lang,
                         history_content_result=history.history_content_result,
                         history_content_result_lang=history.history_content_result_lang,
                         user_name=history.user_nick,
                         history_create_date=datetime.now())
    db.add(db_history)
    db.commit()
    db.refresh(db_history)  # db로부터 다시 읽어들임.
    histories = db.query(History).filter(History.user_no == db_history.user_no).order_by(
        desc(History.history_create_date)).all()
    # histories = db.query(History).filter(History.user_no == db_history.user_no).all()

    return {"res": "true", "histories": histories}  # , "db_history": db_history


@router.post("/historyFavoriteCheck", response_model=HistoryOrm)  # , response_model=HistoryOrm
async def history_favorite_check(req: Request, db: Session = Depends(get_db)):
    info = await req.json()
    print(info)
    db_favorite = None
    if info["favorite_no"] == "0":

        print("3")
        # 히스토리번호로 즐겨찾기 생성
        db_favorite = Favorite(
            user_no=info["user_no"],
            user_email=info["user_email"],
            user_name=info["user_name"],
            favorite_content=info["history_content"],
            favorite_content_lang=info["history_content_lang"],
            favorite_content_result=info["history_content_result"],
            favorite_content_result_lang=info["history_content_result_lang"],
            favorite_create_date=datetime.now(),
            history_no=info["history_no"]
        )
        db.add(db_favorite)
        # db.flush() db.refresh()의 차이: 데이터베이스로 데이터를 즉시 반영하여 최신을 유지할지,
        # 데이터를 데이터베이스로부터 가져와 최신을 유지할지의 차이임. 호출할때 데이터의 일관성을 유지하는 방향성이 save냐 load냐의 차이.
        db.flush()
        # db.refresh(db_favorite)  # db로부터 다시 읽어들임.

        # 즐겨찾기 생성한뒤 요청 들어온 히스토리에 즐겨찾기 번호 업데이트추가해줘야함.
        history = db.query(History).filter(History.user_no == info["user_no"]) \
            .filter(History.history_no == info["history_no"]) \
            .first()

        history.favorite_no = db_favorite.favorite_no
        db.commit()

        histories = db.query(History).filter(History.user_no == info["user_no"]).order_by(
            desc(History.history_create_date)).all()
        favorites = db.query(Favorite).filter(Favorite.user_no == info["user_no"]).order_by(
            desc(Favorite.favorite_create_date)).all()

        return {"res": "true", "histories": histories, "favorites": favorites}  # , "db_history": db_history


    # favorite_no가 0이 아니면== 즐찾되어 있으면, 해제작업(즐찾삭제 및 히스토리 row에서 favorite_no = 0 으로 변경
    else:
        favorite = db.query(Favorite).filter(Favorite.favorite_no == info["favorite_no"]).delete()
        db.query(History).filter(History.history_no == info["history_no"]).update({History.favorite_no: 0})
        db.commit()

        histories = db.query(History).filter(History.user_no == info["user_no"]).order_by(
            desc(History.history_create_date)).all()
        favorites = db.query(Favorite).filter(Favorite.user_no == info["user_no"]).order_by(
            desc(Favorite.favorite_create_date)).all()
        return {"res": "true", "histories": histories, "favorites": favorites}  # , "db_history": db_history



# Read operations
# Get all histories
# @router.get("/historyGet/{user_no}")
# def read_histories(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
#     histories = db.query(History).offset(skip).limit(limit).all()
#     return histories


# 사용자의 번호를 이용해서 이력 내역 가져오기.
# @router.get("/histories/{user_no}", response_model=List[HistoryOrm])
# def read_histories(user_no: int, db: Session = Depends(get_db)):
#     histories = db.query(History).filter(History.user_no == user_no).all()
#     if not histories:
#         raise HTTPException(status_code=404, detail="Histories not found")
#     return histories


# Get history by id. 히스토리 번호(id)를 클라이언트로부터 받아와서 그 내용을 보내주기.
# 필요없을듯. 어차피 개별 내용은 리사이클러뷰 홀더에서 content 를 받으면 끝이라..
# @router.get("/history/{history_no}")
# def read_history(history_no: int, db: Session = Depends(get_db)):
#     history = db.query(History).filter(History.history_no == history_no).first()
#     if not history:
#         raise HTTPException(status_code=404, detail="History not found")
#     return history


# Update operation
# @router.put("/history/{history_no}")
# def update_history(history_no: int, history: HistoryOrm, db: Session = Depends(get_db)):
#     db_history = db.query(History).filter(History.history_no == history_no).first()
#     if not db_history:
#         raise HTTPException(status_code=404, detail="History not found")
#     db_history.user_no = history.user_no
#     db_history.user_email = history.user_email
#     db_history.user_name = history.user_name
#     db_history.history_content = history.history_content
#     db_history.history_create_date = history.history_create_date
#     db.commit()
#     return {"message": "History updated"}




# Delete operation
# @router.delete("/history/{history_no}")
@router.post("/historyDelete")
def delete_history(cli_info: HistoryOrm, db: Session = Depends(get_db)):
    db_history = db.query(History).filter(History.history_no == cli_info.history_no) \
        .filter(History.user_no == cli_info.user_no).first()
    if not db_history:
        # return {"res": "false"}
        raise HTTPException(status_code=404, detail="History not found")
    db.delete(db_history)
    db.commit()
    return {"res": "true"}


@router.post("/historyDeleteAll/{user_no}")
def delete_history_all(user_no: int, db: Session = Depends(get_db)):
    db.query(History).filter(History.user_no == user_no).delete()
    db.commit()

    return {"res": "true", "histories": []}






# @app.get("/chat1/{chat11}")
# def read_chat(chat11: int):
#     print(past_user_inputs)
#     # generated_responses
#     return {"past_user_inputs ": chat11, "generated_responses": "generated_responses"}


# @router.get("/")
# async def read_root():
#     return [{"id": 1}, {"id": 2}]
#
# @router.get("/{user_id}")
# async def read_user(user_id: int):
#     return {"id": user_id, "full_name": "Danny Manny", "email": "danny.manny@gmail.com"}
#
# @router.get("/detail")
# async def read_users(q: Optional[str] = Query(None, max_length=50)):
#     results = {"users": [{"id": 1}, {"id": 2}]}
#     if q:
#         results.update({"q": q})
#     return results
#
# @router.post("/add")
# async def add_user(user: UserModel):
#     return {"full_name": user.first_name+" "+user.last_name}
#
# @router.put("/update")
# async def read_user(user: UserModel):
#     return {"id": user.user_id, "full_name": user.first_name+" "+user.last_name, "email": user.email}
#
# @router.delete("/{user_id}/delete")
# async def read_user(user_id: int):
#     return {"id": user_id, "is_deleted": True}
