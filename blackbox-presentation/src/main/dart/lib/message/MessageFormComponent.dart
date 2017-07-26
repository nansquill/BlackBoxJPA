import 'dart:async';

import 'package:angular2/angular2.dart';

import '../_models/CategoryModel.dart';
import '../user/UserFormComponent.dart';
import '../category/CategoryFormComponent.dart';
import '../_services/UserService.dart';
import '../_models/UserModel.dart';

@Component(
  selector: 'message-form',
  templateUrl: 'MessageFormComponent.html',
  directives: const [COMMON_DIRECTIVES, UserFormComponent, CategoryFormComponent],
  providers: const [UserService]
)
class MessageFormComponent {
  @Input('message')
  Message message;
  @Input('mode')
  String mode;
  final _deleteRequest = new StreamController<Message>();
  final _updateRequest = new StreamController<Message>();
  final _createRequest = new StreamController<Message>();
  @Output()
  Stream<Message> deleteRequest() => _deleteRequest.stream;
  @Output()
  Stream<Message> updateRequest() => _updateRequest.stream;
  @Output()
  Stream<Message> createRequest() => _createRequest.stream;
  final UserService _usrService;

  MessageFormComponent(this._usrService);

  Future<Null> gotoEdit(Message msg) async {
    try {
      User user = await this._usrService.getUser();
      if(!user || mode == "edit") { 
        print("[Error] Mode invalid or user not found");
        return;
      }
      if(user.username == msg.user.username || user.username == "admin")
      {
        print("[Info] Change to edit view");
        message = msg;
        mode = "edit";
      }
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  dynamic update() {
    try {
      if(mode == "edit") {
        print("[Info] Message update, change to show view");
        mode = "view";
        _updateRequest.add(message);
      }
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  dynamic delete() {
    try {
      if(mode == "edit") {
        print("[Info] Message delete, change to show view");
        mode = "view";
        _deleteRequest.add(message);
      }
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  dynamic create() {
    try {
      if(mode == "create") {
        print("[Info] Message create, change to show view");
        mode = "view";
        _createRequest.add(message);
      }
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  dynamic _handleException(dynamic ex) {
		print("MessageFormComponent");
		print(ex);  
	}
}