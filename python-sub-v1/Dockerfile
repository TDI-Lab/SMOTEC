FROM python:latest

ADD mqtt_subscriber.py .

RUN pip install paho-mqtt

CMD [ "python", "./mqtt_subscriber.py" ]
