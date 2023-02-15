from transformers import pipeline
unmasker = pipeline('fill-mask', model='xlm-roberta-large')
unmasker("Hello I'm a <mask> model.")

[{'score': 0.10563907772302628,
  'sequence': "Hello I'm a fashion model.",
  'token': 54543,
  'token_str': 'fashion'},
 {'score': 0.08015287667512894,
  'sequence': "Hello I'm a new model.",
  'token': 3525,
  'token_str': 'new'},
 {'score': 0.033413201570510864,
  'sequence': "Hello I'm a model model.",
  'token': 3299,
  'token_str': 'model'},
 {'score': 0.030217764899134636,
  'sequence': "Hello I'm a French model.",
  'token': 92265,
  'token_str': 'French'},
 {'score': 0.026436051353812218,
  'sequence': "Hello I'm a sexy model.",
  'token': 17473,
  'token_str': 'sexy'}]

from transformers import AutoTokenizer, AutoModelForMaskedLM

tokenizer = AutoTokenizer.from_pretrained('xlm-roberta-large')
model = AutoModelForMaskedLM.from_pretrained("xlm-roberta-large")

# prepare input
text = "Replace me by any text you'd like."
encoded_input = tokenizer(text, return_tensors='pt')

# forward pass
output = model(**encoded_input)
