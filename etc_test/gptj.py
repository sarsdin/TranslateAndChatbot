from transformers import AutoTokenizer, AutoModelForCausalLM
from transformers import pipeline
import torch
import gc
import time
start = time.time()

USE_CUDA = torch.cuda.is_available()
device = torch.device('cuda:0' if USE_CUDA else 'cpu')
# gc.collect()
# torch.cuda.empty_cache()
# ㅎㅎ

# tokenizer = AutoTokenizer.from_pretrained("togethercomputer/GPT-JT-6B-v1")
# model = AutoModelForCausalLM.from_pretrained(
#     "togethercomputer/GPT-JT-6B-v1", torch_dtype='auto', low_cpu_mem_usage=True,
# )
# load_in_8bit=True, device_map='auto'

with torch.no_grad():
    # config = transformers.GPTJConfig.from_pretrained("EleutherAI/gpt-j-6B")
    tokenizer = AutoTokenizer.from_pretrained("EleutherAI/gpt-j-6B")
    model = AutoModelForCausalLM.from_pretrained(
        "hivemind/gpt-j-6B-8bit",  load_in_8bit=True, device_map="auto"
    ).to(device)

# with torch.set_grad_enabled(False):
#     # config = transformers.GPTJConfig.from_pretrained("EleutherAI/gpt-j-6B")
#     tokenizer = AutoTokenizer.from_pretrained("iliemihai/GPT-JT-6B-v1-8bit")
#     model = AutoModelForCausalLM.from_pretrained("iliemihai/GPT-JT-6B-v1-8bit", torch_dtype=torch.float16, low_cpu_mem_usage=True, load_in_8bit=True, device_map="auto")

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
    revision="float16",
    device_map='auto',
    torch_dtype='auto',
    model_kwargs={
        # 'load_in_8bit': True,
        # 'device_map': 'auto'
    },
    tokenizer=tokenizer,
    device=0,
    framework='pt'
)
prompt = "The following is a conversation with an AI assistant. " \
         "The assistant is helpful, creative, clever, and very friendly.\
        Human: Hello, who are you? \
        AI: I am an AI created by OpenAI. How can I help you today? \
        Human: I'd like to cancel my subscription. \
        AI:"

text = pipe(prompt, do_sample=True, max_length=50, pad_token_id=50256,
            num_return_sequences=3, top_k=50, top_p=0.95
            )
# num_beams=4, no_repeat_ngram_size=2, temperature=0.75, early_stopping=True,
print(text)
time.sleep(0.9)
end = time.time()
print("Time consumed in working: ", end - start)

text = pipe("아인슈타인은", do_sample=True, max_length=50, pad_token_id=50255,
            num_return_sequences=2, top_k=50, top_p=0.95
            )
print(text)
time.sleep(0.9)
end = time.time()
print("Time consumed in working: ", end - start)


# 텍스트 생성 기능위주인듯한데... 대화 기능을 찾아보자..

# print(f"gen_text: {pipe}")
# print(f"gen_text: {inputs_ids['input_ids']}")
# print(f"gen_text: {inputs_ids}")
# print(f"gen_text: {prediction_logits}")
# print(f"gen_text: {outputs}")
