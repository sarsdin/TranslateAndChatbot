from fastapi import APIRouter
import ReqApi

router = APIRouter()
router.include_router(ReqApi.router)
# router.include_router(user.router)
