from transformers import AutoTokenizer, AutoModelForCausalLM
from transformers import pipeline
import torch
import gc
import time

from urllib import response
import requests
import json

start = time.time()

# device = "cuda" if torch.cuda.is_available() else "cpu"

USE_CUDA = torch.cuda.is_available()
device = torch.device('cuda:0' if USE_CUDA else 'cpu')
# gc.collect()
# torch.cuda.empty_cache()

# print('CUDA 사용 가능 여부 :', USE_CUDA)
# print('현재 사용 device :', device)
# print('CUDA Index :', torch.cuda.current_device())
# print('GPU 이름 :', torch.cuda.get_device_name())
# print('GPU 개수 :', torch.cuda.device_count())

# tokenizer = AutoTokenizer.from_pretrained("EleutherAI/polyglot-ko-5.8b")
# model = AutoModelForCausalLM.from_pretrained("EleutherAI/polyglot-ko-5.8b")
# tokenizer = AutoTokenizer.from_pretrained("EleutherAI/polyglot-ko-3.8b")
# model = AutoModelForCausalLM.from_pretrained("EleutherAI/polyglot-ko-3.8b")

# tokenizer = AutoTokenizer.from_pretrained("EleutherAI/polyglot-ko-1.3b",
#     bos_token='[BOS]', eos_token='[EOS]', unk_token='[UNK]', pad_token='[PAD]', mask_token='[MASK]')
# model = AutoModelForCausalLM.from_pretrained(
#     "EleutherAI/polyglot-ko-1.3b", torch_dtype='auto', low_cpu_mem_usage=True)

tokenizer = AutoTokenizer.from_pretrained("EleutherAI/polyglot-ko-3.8b",
                                          bos_token='[BOS]', eos_token='[EOS]', unk_token='[UNK]', pad_token='[PAD]',
                                          mask_token='[MASK]'
                                          )
model = AutoModelForCausalLM.from_pretrained(
    "EleutherAI/polyglot-ko-3.8b", torch_dtype='auto', low_cpu_mem_usage=True)

# tokenizer = AutoTokenizer.from_pretrained("EleutherAI/polyglot-ko-5.8b",
#                                           bos_token='[BOS]', eos_token='[EOS]', unk_token='[UNK]', pad_token='[PAD]',
#                                           mask_token='[MASK]'
#                                           )
# model = AutoModelForCausalLM.from_pretrained(
#     "EleutherAI/polyglot-ko-5.8b", torch_dtype='auto', low_cpu_mem_usage=True)

# prompt = "GPTNeoX20B is a 20B-parameter autoregressive Transformer model developed by EleutherAI."

# input_ids = tokenizer(prompt, return_tensors="pt").input_ids
# input_ids = tokenizer(prompt, return_tensors="pt").to(device).input_ids
# .input_ids는 리턴되는 객체(=튜플)의 요소중 하나.
# inputs_ids = tokenizer(prompt, return_tensors="pt").to(device)
# model = model.to(device)
# outputs = model(inputs_ids)
# prediction_logits = outputs.logits

# gen_tokens = model.generate(
#     input_ids,
#     do_sample=True,
#     temperature=0.9,
#     max_length=150,
# )
# gen_text = tokenizer.batch_decode(gen_tokens)[0]

# 바로 위의 코드들을 pipeline()메소드로 축약시킨 코드가 가능
pipe = pipeline(
    'text-generation',
    model=model,
    tokenizer=tokenizer,
    device=0,
    framework='pt'
)
prompt1 = """\
The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.

Human: 오늘 날씨 어때? 
AI: 좋습니다. 당신의 하루는 오늘 어땠나요? 
Human: 난 오늘 꽤 괜찮았지~ 
AI: 그렇군요! 모르는 것 있으면 물어보세요. 친절히 알려드리겠습니다."""

# text = pipe(prompt, do_sample=True,  pad_token_id=50256,
#             num_return_sequences=3, top_k=50, top_p=0.95, max_new_tokens=50
#             )
# num_beams=4, no_repeat_ngram_size=2, temperature=0.75, early_stopping=True,
# max_length=50,
# print(text)
# time.sleep(0.9)
# end = time.time()
# print("Time consumed in working: ", end - start)

# text = pipe("아인슈타인은", do_sample=True, max_length=50, pad_token_id=50255,
#             num_return_sequences=2, top_k=50, top_p=0.95
#             )
# print(text)
# time.sleep(0.9)
# end = time.time()
# print("Time consumed in working: ", end - start)


# 텍스트 생성 기능위주인듯한데... 대화 기능을 찾아보자..

# print(f"gen_text: {pipe}")
# print(f"gen_text: {inputs_ids['input_ids']}")
# print(f"gen_text: {inputs_ids}")
# print(f"gen_text: {prediction_logits}")
# print(f"gen_text: {outputs}")

# Only recent response text will return
def processing_response(response_text: str ) -> str:
    ret_text = ""
    for i in range(0, len(response_text)):
        if response_text[i] == "\n" or response_text[i:i + 7] == "Human: " or response_text[i:i + 4] == "AI: ":
            break
        ret_text += response_text[i]
    return ret_text.strip()


url = "https://eleuther-ai-gpt-j-6b-float16-text-generation-api-ainize-team.endpoint.ainize.ai/predictions/text-generation"
# base_prompt = get_base_prompt()
base_prompt = prompt1
headers = {"Content-Type": "application/json"}
# End chat when user types "exit"
while True:
    chat = input("Human: ")
    if chat == "exit":
        break
    prompt = f"{base_prompt}\nHuman: {chat}\nAI:"

    # data == string type dumps()는 json객체를 json문자열로 변환시켜줌.
    data = json.dumps({
        "text_inputs":prompt,
        "temperature":0.9,
        "top_p":0.95,
        "repetition_penalty":0.8,
        "do_sample":True,
        "top_k":50,
        "length":50,
    })
    # loads()는 json문자열을 json객체로 변환시켜줌.
    jo = json.loads(data)
    # text = pipe(
    #     jo['text_inputs'],
    #     do_sample=jo['do_sample'],
    #     temperature=jo['temperature'],
    #     top_p=jo['top_p'],
    #     top_k=jo['top_k'],
    #     max_new_tokens=jo['length'],
    #     repetition_penalty=jo['repetition_penalty'],
    #     pad_token_id=50256
    # )
    text = pipe(
        prompt,
        do_sample=True,
        temperature=0.9,
        top_p=0.95,
        top_k=50,
        max_new_tokens=40,
        repetition_penalty=0.8,
        pad_token_id=50256
    )

    # res = requests.post(url, headers=headers, data=data)
    # if res.status_code == 200:
    # response_text = res.json()[0] # response에서 json응답일경우 바로 딕셔너리로 변환하고 0이라는 키의 값을 뽑아냄
    print(text[0])
    response_text = text[0]['generated_text']
    print("AI:", processing_response(response_text[len(prompt):]))



