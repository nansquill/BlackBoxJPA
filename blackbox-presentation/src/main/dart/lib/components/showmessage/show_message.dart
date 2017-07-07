

import 'package:angular2/angular2.dart';
import 'dart:html';
import 'dart:convert';
import 'package:blackbox/model/message.dart';

@Component(selector: 'show-message', templateUrl: 'show_message.html')
class ShowMessage {

  List<Message> liste = [];
  

  ShowMessage(){
  }
  
  void getMessages() {
  	var requestHeaders = {
      'Content-Type':'application/json',
      'Accept':'application/json'};
  	 HttpRequest.getString("../rest/messages")
  	 .then((String fileContents) {
  	 print(fileContents.length);
  	 liste = JSON.decode(fileContents);
  	 })
  	 .catchError((Error error) {
  	 	print(error.toString());
  	 });
  	 
  }
  

}

