import torch
import ReqApi
from transformers import AutoTokenizer, AutoModelForCausalLM
from fastapi import FastAPI, Request
from typing import Union
from pydantic import BaseModel, EmailStr

# 서버 만들기
app = FastAPI()

device = 'cuda' if torch.cuda.is_available() else 'cpu'

print("모델 로딩 시작.")
tokenizer = None
model = None
with torch.no_grad():
    tokenizer = AutoTokenizer.from_pretrained(
        "/home/.cache/kk",
        # 'kakaobrain/kogpt', revision='KoGPT6B-ryan1.5b-float16',
        bos_token='[BOS]', eos_token='[EOS]', unk_token='[UNK]', pad_token='[PAD]', mask_token='[MASK]'
    )
    model = AutoModelForCausalLM.from_pretrained(
        "/home/.cache/kk",
        # 'kakaobrain/kogpt', revision='KoGPT6B-ryan1.5b-float16',
        pad_token_id=tokenizer.eos_token_id,
        torch_dtype=torch.float16, low_cpu_mem_usage=True
    ).to(device='cuda', non_blocking=True)
_ = model.eval()
print("모델 로딩 완료.")


if __name__ == '__chat__':
    print('chat server started.')


@app.get("/")
def read_root():
    return {
        "Hello": "World"
    }


@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id ": item_id, "q": q}


class Item(BaseModel):
    # content: List[str]
    user_input: str
    # password: str
    # email: EmailStr
    # full_name: Optional[str] = None

# , response_model=Item, response_model_exclude_unset=True
# 요청이 들어오면 알아서 요청안의 요소중 이름이 같은 요소명을 item class의 이름이 같은 변수로 맵핑하여 인자로 던져줌.
@app.post("/writeChat")
def writeChat(item: Item, req: Request):
    # past_user_inputs
    # generated_responses
    # await req.json()
    print("hello??")
    if item.user_input == "--초기화":
        초기화()
        return {"past_user_inputs": past_user_inputs, "generated_responses": generated_responses}

    process_req(item.user_input)
    return {"past_user_inputs": past_user_inputs, "generated_responses": generated_responses}


@app.get("/getChat")
def getChat():
    print(past_user_inputs)
    # generated_responses
    return {"past_user_inputs": past_user_inputs, "generated_responses": generated_responses}



@app.post("/joinComplete")
def joinComplete():
    print("d")

    return {"past_user_inputs": past_user_inputs, "generated_responses": generated_responses}



# @app.get("/chat1/{chat11}")
# def read_chat(chat11: int):
#     print(past_user_inputs)
#     # generated_responses
#     return {"past_user_inputs ": chat11, "generated_responses": "generated_responses"}


base_prompt = """
다음 문장들은 Bot과의 대화문이다. Bot의 성격은 희망적, 창조적이며 영리하고 친근하다.

User: 오늘 날씨 어때?
Bot: 좋습니다. 당신의 하루는 오늘 어땠나요? 
User: 난 오늘 꽤 괜찮았지~ 
Bot: 그렇군요! 모르는 것 있으면 물어보세요. 친절히 알려드리겠습니다.
"""


# 파라메터로 받은 문장의 5번째 시퀀스("BOT: "이후)부터 끝까지 반복하면서 줄바꿈, User: , Bot: 이 나오면 멈추고, 거기까지의 문장을 반환함.
def processing_response(response_text: str) -> str:
    ret_text = ""
    for i in range(5, len(response_text)):
        if response_text[i] == "\n" or response_text[i:i + 6] == "User: " or response_text[i:i + 5] == "Bot: ":
            break
        ret_text += response_text[i]
    return ret_text.strip()


past_user_inputs = []
generated_responses = []
past_user_inputs.append("다음 문장들은 Bot과의 대화문이다.")
generated_responses.append("대화문속 Bot의 성격은 희망적, 창조적이며 영리하고 친근하다.")
past_user_inputs.append("오늘 날씨 어때?")
generated_responses.append("나쁘지 않습니다. 당신의 하루는 어땠나요?")
past_user_inputs.append("오늘 난 꽤 좋은 하루였어~")
generated_responses.append("좋은 하루였군요! 모르는 것이 있으면 물어보세요. 친절히 알려드리겠습니다.")


def 초기화():
    print("대화를 초기화 합니다.")
    past_user_inputs.clear()
    generated_responses.clear()
    past_user_inputs.append("다음 문장들은 Bot과의 대화문이다.")
    generated_responses.append("대화문속 Bot의 성격은 희망적, 창조적이며 영리하고 친근하다.")
    past_user_inputs.append("오늘 날씨 어때?")
    generated_responses.append("나쁘지 않습니다. 당신의 하루는 어땠나요?")
    past_user_inputs.append("오늘 난 꽤 좋은 하루였어~")
    generated_responses.append("좋은 하루였군요! 모르는 것이 있으면 물어보세요. 친절히 알려드리겠습니다.")


def process_req(user_input: str) -> str:
    print("hi?")

    # user_input = input(">> User:")
    # 우선 인풋받은 내용을 인코딩하여 파이토치 텐서객체로 반환
    text_idx = tokenizer.encode(tokenizer.eos_token + "User: " + user_input + "\n", return_tensors='pt')

    # 생성된 봇의 텍스트의 배열 크기만큼 반복함. -1은 i의 인덱스범위가 크기보다 1작아야하기때문. 예) 크기10일때 마지막 인덱스는 9이기 때문.
    # -3은 최근 3개의 생성 텍스트까지만 기억하고 반복하겠다는 뜻. step을 -1로 줘서 최근 텍스트부터 과거 텍스트 순으로 반복진행함.
    for i in range(len(generated_responses) - 1, len(generated_responses) - 5, -1):
        if i < 0:
            break
        # 그리고, 이전에 생성된 봇의 텍스트를 인코딩하여 파이토치 텐서객체로 반환
        encoded_vector = tokenizer.encode(tokenizer.eos_token + "Bot: " + generated_responses[i] + "\n",
                                          return_tensors='pt')
        # 입력 텍스트와 최근 생성했던 봇의 텍스트의 길이 합이 설정 값(500)보다 작으면, 최근 생성 텍스트를 현재 입력 텍스트에 추가함
        if text_idx.shape[-1] + encoded_vector.shape[-1] < 700:
            text_idx = torch.cat([encoded_vector, text_idx],
                                 dim=-1)  # dim=-1 negative indexing 즉, 마지막차원. dim=-2 는 이전 차원.
            # print("text_idx2: ")
            # print(text_idx)
        else:
            break

        encoded_vector = tokenizer.encode(tokenizer.eos_token + "User: " + past_user_inputs[i] + "\n",
                                          return_tensors='pt')
        # 위에서 합쳐진 text_idx 값과 유저의 직전 인풋값을 합쳐서 설정 값(500)보다 작으면, 그 인풋값 또한 현재 입력 텍스트에 추가해줌.
        # 이런식으로 for 문이 끝날때까지 뒤에서부터 차례대로 제한 설정값 한도내에서 최대한 추가해주는 작업임.
        if text_idx.shape[-1] + encoded_vector.shape[-1] < 700:
            text_idx = torch.cat([encoded_vector, text_idx], dim=-1)
            # print("text_idx3: ")
            # print(text_idx)
        else:
            break
    text_idx_decoded = text_idx
    text_idx = text_idx.to(device)
    inference_output = model.generate(
        text_idx,
        do_sample=True,
        temperature=0.8,
        max_new_tokens=50,
        # num_beams=5,
        # top_k=20,
        # no_repeat_ngram_size=4,
        # length_penalty=0.65,
        # repetition_penalty=2.0,
    )
    # print("inference_output: ")
    # print(inference_output)
    inference_output = inference_output.tolist()
    # print("inference_output.tolist(): ")
    # print(inference_output)
    bot_response = tokenizer.decode(inference_output[0][text_idx.shape[-1]:], skip_special_tokens=True)
    # print(f"Bot: {bot_response}")

    # response_text = text[0]['generated_text']
    # text_idx_decoded = tokenizer.decode(text_idx_decoded[0], skip_special_tokens=True)
    # print("text_idx_decoded: ")
    # print(text_idx_decoded)
    # prompt = f"{text_idx_decoded}\nBot:"

    #
    result_text = processing_response(bot_response)
    print("Bot: ", result_text)
    past_user_inputs.append(user_input)
    generated_responses.append(result_text)

    return result_text
