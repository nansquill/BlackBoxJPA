import 'dart:convert';
class Messages {
  int id;
  String content;
  String headline;
  DateTime publishedOn;
  boolean isOnline;

  String toJSON() {
    return JSON.encode({
      'content':content,
      'headline':headline
    });
  }

}