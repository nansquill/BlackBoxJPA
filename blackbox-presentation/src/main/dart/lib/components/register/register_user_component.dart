

import 'package:angular2/angular2.dart';
import 'dart:html';

@Component(selector: 'register-user', templateUrl: 'register_user_component.html')
class RegisterUser {


  CreateUser(){
  }

  void postUser(dynamic e){
    e.preventDefault();
    var requestHeaders = {
      'Content-Type':'application/json',
      'Accept':'application/json'
    };
    HttpRequest.request("../rest/register",method: "POST",sendData: model.toJSON(),requestHeaders: requestHeaders).catchError((n)=>print(n));
  }
}
