'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotifications = functions.database.ref(`Users/{user_id}/notifications/{notification_id}`).onWrite((data, context) => {
    const user_id = context.params.user_id;
    const notification_id = context.params.notification_id;

   const notification_content = admin.database().ref(`/Users/${user_id}/notifications/${notification_id}`).once('value');

   return notification_content.then(notification_result => {
        const sender_id = notification_result.val().notification_from;
        const notification_title = notification_result.val().notification_title;
        const notification_body = notification_result.val().notification_text;

        //get notification partner id
        const get_device_token = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

        return get_device_token.then(result => {
            const device_token = result.val();

            const payload = {
                notification : {
                    title: notification_title,
                    body: notification_body,
                    icon: "default",
                    sound: "default",
                    vibrate: "default",
                    android_channel_id: "notification_channel",
                }, 
                data: {
                    sender_id: sender_id
                }
            };

            return admin.messaging().sendToDevice(device_token, payload).then(response => {
                console.log('Notification was sent!');                
            });
        });
   });
});
