import cv2

face_cascade = cv2.CascadeClassifier('./haarcascade_frontalface_default.xml')


class VideoCamera(object):
    def __init__(self):
        # capturing video
        self.video = cv2.VideoCapture(0)

    def __del__(self):
        # releasing camera
        self.video.release()

    def get_frame(self):
        # extracting frames
        succes = False
        ret, frame = self.video.read()
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        return succes, frame
