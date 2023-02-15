from transformers import GPTNeoXForCausalLM, GPTNeoXTokenizerFast

model = GPTNeoXForCausalLM.from_pretrained("EleutherAI/gpt-neox-20b")
tokenizer = GPTNeoXTokenizerFast.from_pretrained("EleutherAI/gpt-neox-20b")

prompt = "GPTNeoX20B is a 20B-parameter autoregressive Transformer model developed by EleutherAI."

input_ids = tokenizer(prompt, return_tensors="pt").input_ids

gen_tokens = model.generate(
    input_ids,
    do_sample=True,
    temperature=0.9,
    max_length=100,
)
gen_text = tokenizer.batch_decode(gen_tokens)[0]

print(f"gen_text: {gen_text}")

# from transformers import GPTNeoXTokenizerFast, GPTNeoXForCausalLM, GPTNeoXConfig
# import torch
#
# tokenizer = GPTNeoXTokenizerFast.from_pretrained("EleutherAI/gpt-neox-20b")
# config = GPTNeoXConfig.from_pretrained("EleutherAI/gpt-neox-20b")
# config.is_decoder = True
# model = GPTNeoXForCausalLM.from_pretrained("EleutherAI/gpt-neox-20b", config=config)
#
# inputs = tokenizer("Hello, my dog is cute", return_tensors="pt")
# outputs = model(**inputs)
#
# prediction_logits = outputs.logits