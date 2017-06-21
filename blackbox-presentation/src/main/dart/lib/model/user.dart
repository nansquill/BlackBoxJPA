import 'dart:convert';
class User {
  int id;
  String username;
  String password;
  DateTime createdOn;
  DateTime lastVisitedOn;
  bool isAdmin;

  String toJSON() {
    return JSON.encode({
      'username':username,
      'password':password,
      'isAdmin':isAdmin
    });
  }

}