import os
os.environ["CUDA_VISIBLE_DEVICES"] = "0"
from flask import Flask, Response
import cv2
import videocamera as vc
import numpy as np
from lib.yolo_inference import yolo_infer2
from lib.emotic import Emotic
from PIL import Image

app = Flask(__name__)

video_camera = vc.VideoCamera()


def gen(camera):
    emotion_i = []
    ret, img = camera.get_frame()
    # setting up the categories
    # our model outputs numbers as categories, so we much have a mapping from the numbers to the emotions
    cat = ['Affection', 'Anger', 'Annoyance', 'Anticipation', 'Aversion', 'Confidence', 'Disapproval', 'Disconnection', \
           'Disquietment', 'Doubt/Confusion', 'Embarrassment', 'Engagement', 'Esteem', 'Excitement', 'Fatigue', 'Fear',
           'Happiness',
           'Pain', 'Peace', 'Pleasure', 'Sadness', 'Sensitivity', 'Suffering', 'Surprise', 'Sympathy', 'Yearning']
    cat2ind = {}
    ind2cat = {}
    for idx, emotion in enumerate(cat):
        cat2ind[emotion] = idx
        ind2cat[idx] = emotion

    vad = ['Valence', 'Arousal', 'Dominance']
    ind2vad = {}
    for idx, continuous in enumerate(vad):
        ind2vad[idx] = continuous

    # we need to normalise the demo image using the statistics of the training data the model was trained on
    context_mean = [0.4690646, 0.4407227, 0.40508908]
    context_std = [0.2514227, 0.24312855, 0.24266963]
    body_mean = [0.43832874, 0.3964344, 0.3706214]
    body_std = [0.24784276, 0.23621225, 0.2323653]
    context_norm = [context_mean, context_std]
    body_norm = [body_mean, body_std]

    # pay attention to the arguments to the function,
    # it takes a path to the image location, a result and model directory
    # the result directory also contains threshold information to make decisions(please do not delete)
    # write_op will write the output to a new file and return the location(2nd return variable)
    # return_op will return the processed image as the 3rd return variable

    xs, _, r = yolo_infer2(img, '/Users/jurriaan/git/gh/ca_emotion/experiment/results/',
                           '/Users/jurriaan/git/gh/ca_emotion/experiment/models/', context_norm, body_norm, ind2cat, ind2vad, write_op=False,
                           return_op=True)

    # the inference function returns a dict, top-level key is the bounding box. Each bounding box has sub keys:
    #                1. cont - for continuous V-A-D emotion values
    #                2. cat - for the top k categorical emotions
    return xs


@app.route('/emotion')
def ret_emotion():
    return Response(str(gen(video_camera)))


if __name__ == '__main__':
    app.run()
