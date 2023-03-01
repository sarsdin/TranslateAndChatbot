import random
import secrets

"""암호화 모듈을 이용한 난수코드 생성기"""
def generate_verification_code(length=6):
    alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    verification_code = ''.join(secrets.choice(alphabet) for i in range(length))
    return verification_code


"""일반적인 코드 생성기"""
def generate_code():
    code = ""
    for i in range(6):
        code += str(random.randint(0, 9))
    return code


