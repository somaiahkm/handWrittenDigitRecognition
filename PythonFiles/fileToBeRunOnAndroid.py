from os.path import dirname, join
from com.chaquo.python import Python
import cv2
import pickle
import base64
import io
from PIL import Image
def main(s):
    files_dir = str(Python.getPlatform().getApplication().getFilesDir())
    filename = join(dirname(files_dir), "temp.png")
    decoded_data = base64.b64decode(s)
    with open(filename, 'wb') as f:
        f.write(decoded_data)
    test_img = cv2.imread(filename, cv2.IMREAD_GRAYSCALE)
    img_resized = cv2.resize(test_img, (28, 28), interpolation=cv2.INTER_LINEAR)
    img_resized = cv2.bitwise_not(img_resized)
    new_test = img_resized.reshape(-1)
    filename1 = join(dirname(__file__), 'model_pickle.pkl')
    with open(filename1, 'rb') as fn:
       newm = pickle.load(fn)
    ans = newm.predict([new_test])

    return ans

