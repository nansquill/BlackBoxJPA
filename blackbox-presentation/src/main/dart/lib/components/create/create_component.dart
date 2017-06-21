

import 'package:angular2/angular2.dart';
import 'dart:html';
import 'package:blackbox/model/message.dart';

@Component(selector: 'create-message', templateUrl: 'create_component.html')
class CreateMessages {

  Message model;

  CreateMessage(){
    model = new Message();
  }

  void postMessages(dynamic e){
    e.preventDefault();
    var requestHeaders = {
      'Content-Type':'application/json',
      'Accept':'application/json'
    };
    HttpRequest.request("../rest/messages",method: "POST",sendData: model.toJSON(),requestHeaders: requestHeaders).catchError((n)=>print(n));
  }
}
