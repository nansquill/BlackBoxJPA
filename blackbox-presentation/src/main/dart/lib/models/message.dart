import 'dart:convert';
class Message {
  int id;
  String content;
  String headline;
  DateTime publishedOn;

  Message(this.id, this.content, this.headline, this.publishedOn);

  factory Message.fromJson(Map<String, dynamic> msg) => new Message(msg['id'], msg['content'], msg['headline'], msg['published_on']);

  String toJSON() {
    return JSON.encode({
      'content':content,
      'headline':headline,
      'published_on': publishedOn,
      'id': id
    });
  }

}