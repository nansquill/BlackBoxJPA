

import 'package:angular2/angular2.dart';
import 'dart:html';
import 'package:blackbox/model/messages.dart';

@Component(selector: 'create-messages', templateUrl: 'create_component.html')
class CreateMessages {

  Messages model;

  CreateMessages(){
    model = new Messages();
  }

  void postNews(dynamic e){
    e.preventDefault();
    var requestHeaders = {
      'Content-Type':'application/json',
      'Accept':'application/json'
    };
    HttpRequest.request("../rest/messages",method: "POST",sendData: model.toJSON(),requestHeaders: requestHeaders).catchError((n)=>print(n));

  }
}