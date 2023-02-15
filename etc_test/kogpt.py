from transformers import AutoTokenizer, AutoModelForCausalLM
from transformers import pipeline
import torch
import gc
import time

# import os
# os.environ['TRANSFORMERS_CACHE'] = '/home/.cache/huggingface/hub'

# from transformers.utils.hub import move_cache
# move_cache()

from urllib import response
import requests
import json

# USE_CUDA = torch.cuda.is_available()
# device = torch.device('cuda:0' if USE_CUDA else 'cpu')
print("모델 로딩 시작.")

# with torch.no_grad():
tokenizer = AutoTokenizer.from_pretrained(
    "/home/.cache/kk",
    # 'kakaobrain/kogpt', revision='KoGPT6B-ryan1.5b',
    bos_token='[BOS]', eos_token='[EOS]', unk_token='[UNK]', pad_token='[PAD]', mask_token='[MASK]'
    # ,cache_dir="/home/.cache/huggingface/hub"
)
model = AutoModelForCausalLM.from_pretrained(
    "/home/.cache/kk",
    # 'kakaobrain/kogpt', revision='KoGPT6B-ryan1.5b',
    pad_token_id=tokenizer.eos_token_id,
    torch_dtype=torch.float16, low_cpu_mem_usage=True
    # , cache_dir="/home/.cache/huggingface/hub"
).to(device='cuda', non_blocking=True)
_ = model.eval()

print("모델 로딩 완료.")


# def gpt(prompt):
#     with torch.no_grad():
#         tokens = tokenizer.encode(prompt, return_tensors='pt').to(device='cuda', non_blocking=True)
#         gen_tokens = model.generate(tokens, do_sample=True, temperature=0.8, max_new_tokens=50)
#         generated = tokenizer.batch_decode(gen_tokens)[0]
#     return generated


def gpt(prompt):
    # generated = ""
    # with torch.no_grad():

    text1 = pipe(
        prompt,
        do_sample=True,
        temperature=0.8,
        # top_p=0.95,
        # top_k=50,
        max_new_tokens=50,
        # repetition_penalty=0.8,
        # pad_token_id=50256
    )
    generated = text1[0]['generated_text']

    return generated


# usr_input = ""
base_prompt = """
다음 문장들은 인공지능과의 대화문이다. 인공지능의 성격은 희망적, 창조적이며 영리하고 친근하다.

설정목: 오늘 날씨 어때?
Ai: 좋습니다. 당신의 하루는 오늘 어땠나요? 
설정목: 난 오늘 꽤 괜찮았지~ 
Ai: 그렇군요! 모르는 것 있으면 물어보세요. 친절히 알려드리겠습니다.
"""
base_prompt2 = """
다음 문장들은 인공지능과의 대화문이다. 인공지능의 성격은 희망적, 창조적이며 영리하고 친근하다.

설정목: 오늘 날씨 어때?
Ai: 좋습니다. 당신의 하루는 오늘 어땠나요? 
설정목: 난 오늘 꽤 괜찮았지~ 
Ai: 그렇군요! 모르는 것 있으면 물어보세요. 친절히 알려드리겠습니다.
"""
# text = gpt(prompt)
# print(text)

pipe = pipeline(
        'text-generation',
        model=model,
        tokenizer=tokenizer,
        device=0,
        framework='pt'
    )

# 생성된 문장의 첫시퀸스부터 마지막 시퀸스까지 반복문 돌리는데, 중간에 줄바꿈이나 대화의 화자가 바뀌면 중단하고
# 그전까지 반복문에서 추가된 캐릭터 시퀸스를 리턴함
def processing_response(response_text: str ) -> str:
    ret_text = ""
    for i in range(0, len(response_text)):
        if response_text[i] == "\n" or response_text[i:i + 5] == "설정목: " or response_text[i:i + 4] == "Ai: ":
            break
        ret_text += response_text[i]
    return ret_text.strip()

print("반복시퀀시 시작.")
while True:
    print("반복시퀀스 중...")
    # base_prompt = prompt
    chat = input("설정목: ")
    if chat == "exit":
        break
    elif chat == "reset":
        base_prompt = base_prompt2
        chat = "초기화했다."
    prompt = f"{base_prompt}\n설정목: {chat}\nAi:"
    text = gpt(prompt)


    # print(text[0]['generated_text'])
    # print("\n")

    # response_text = text[0]['generated_text']
    response_text = text
    # 생성된 전체 문장 중에 이전까지 생성된 문장(prompt)의 길이 시퀀스부터 마지막 문장까지를 잘라서 파라메터로 씀.
    print("Ai: ", processing_response(response_text[len(prompt):]))
    base_prompt = f"{base_prompt}\n설정목: {chat}\nAi: {response_text}"