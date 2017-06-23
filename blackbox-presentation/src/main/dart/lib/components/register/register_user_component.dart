

import 'package:angular2/angular2.dart';
import 'dart:html';
import 'package:blackbox/models/user.dart';

@Component(selector: 'register-user', templateUrl: 'register_user_component.html')
class RegisterUser {

  User model;

  CreateUser(){
    model = new User();
  }

  void postUser(dynamic e){
    e.preventDefault();
    var requestHeaders = {
      'Content-Type':'application/json',
      'Accept':'application/json'
    };
    HttpRequest.request("../rest/persons",method: "POST",sendData: model.toJSON(),requestHeaders: requestHeaders).catchError((n)=>print(n));
  }
}
