from typing import *

import torch
from transformers import AutoModelForCausalLM, AutoTokenizer

# https://discuss.huggingface.co/t/some-questions-about-gpt-j-inference-using-int8/25174
def load_model_tokenizer(
    pretrained_model_name_or_path,
    model_args: Optional[List[Any]] = None,
    tokenizer_args: Optional[List[Any]] = None,
    state_dict: Optional[Dict[str, torch.Tensor]] = None,
    proxies: Optional[Dict[str, str]] = None,
    output_loading_info: bool = False,
    use_auth_token: Union[bool, str] = False,
    revision: str = "main",
    mirror: Optional[str] = None,
    low_cpu_mem_usage: bool = False,
    torch_dtype: Union[str, torch.dtype] = "auto",
    device_map: Union[str, Dict[str, Union[int, str, torch.device]]] = "auto",
    load_in_8bit: bool = False,
    load_in_8bit_threshold: float = 0.6,
    load_in_8bit_skip_modules: Optional[List[str]] = None,
    subfolder: str = "",
    use_fast_tokenizer: bool = False,
    model_kwargs: Optional[Dict[str, Any]] = None,
    tokenizer_kwargs: Optional[Dict[str, Any]] = None,
):
    if not model_args:
        model_args = []
    if not tokenizer_args:
        tokenizer_args = []
    if not model_kwargs:
        model_kwargs = dict()
    if not tokenizer_kwargs:
        tokenizer_kwargs = dict()

    model = AutoModelForCausalLM.from_pretrained(
        pretrained_model_name_or_path,
        *model_args,
        state_dict=state_dict,
        proxies=proxies,
        output_loading_info=output_loading_info,
        use_auth_token=use_auth_token,
        revision=revision,
        mirror=mirror,
        low_cpu_mem_usage=low_cpu_mem_usage,
        torch_dtype=torch_dtype,
        device_map=device_map,
        load_in_8bit=load_in_8bit,
        load_in_8bit_threshold=load_in_8bit_threshold,
        load_in_8bit_skip_modules=load_in_8bit_skip_modules,
        subfolder=subfolder,
        **model_kwargs
    )
    tokenizer = AutoTokenizer.from_pretrained(
        pretrained_model_name_or_path,
        *tokenizer_args,
        proxies=proxies,
        revision=revision,
        subfolder=subfolder,
        use_fast=use_fast_tokenizer,
        **tokenizer_kwargs
    )

    return model, tokenizer


if __name__ == "__main__":
    import time

    @torch.inference_mode()
    @torch.cuda.amp.autocast()
    def generate_text(model, tokenizer):
        inputs = tokenizer("HuggingFace Transformers is a", return_tensors='pt').to(model.device)

        start = time.time()
        outputs = model.generate(
            input_ids=inputs['input_ids'],
            attention_mask=inputs['attention_mask'],
            max_length=32,
            min_length=8,
            early_stopping=True
        )
        end = time.time()
        generated_text = tokenizer.decode(outputs[0], skip_special_tokens=True).split('\n')[0].strip()
        print(f"generate text: {generated_text}\ntime used: {end-start:.4f}")

    model, tokenizer = load_model_tokenizer(
        pretrained_model_name_or_path="/root/gemsouls_workstation/GemsoulsPersonaChatter/gpc_src/nn_models/core/gptj",
        low_cpu_mem_usage=True,
        device_map="auto",
        revision="float16",
        # load_in_8bit=True,
        torch_dtype="auto"
    )
    model.eval()

    print(f"model memory footprint: {model.get_memory_footprint()}")
    generate_text(model, tokenizer)