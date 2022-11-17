import paho.mqtt.client as mqtt
import time


def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.publish("house/bulb1","on")
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    # client.subscribe([("house/bulb1", 1), ("ep_mqtt/topic2", 1), ("ep_mqtt/topic3", 1)])


def on_message(client, userdata, message):
    print("Message received: " + message.topic + " : " + str(message.payload))
    if message.topic == 'house/bulb1':
        print("Message ")
        #with open('H:\project3\k3s\mqtt_update.txt', 'a+') as f:
           # f.write("received topic2")

def on_publish(client,userdata,result):             #create function for callback
    print("data published \n")
    pass
broker_address = "mosquitto"#"10.41.13.237"  # Broker address
port = 1883  # Broker port
user = "mqtt"                    #Connection username
password = "mqtt"            #Connection password

client = mqtt.Client()  # create new instance
client.username_pw_set(user, password=password) 
client.on_connect = on_connect  # attach function to callback
client.on_publish = on_publish                          #assign function to callback
client.connect(broker_address, port=port)  # connect to broker
while True:
    client.publish("house/bulb1","on")
    client.loop();


#client.loop_forever()
