import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';

import '../_services/UserService.dart';
import '../_services/MessageService.dart';
import '../message/MessageFormComponent.dart';
import '../user/UserFormComponent.dart';

@Component(
  selector: 'app-profile',
  templateUrl: 'ProfileComponent.html',
  providers: const[ROUTER_PROVIDERS, MessageService, UserService],
  directives: const[ROUTER_DIRECTIVES, MessageFormComponent, UserFormComponent]
)
class ProfileComponent implements OnInit{
  final UserService _usrService;
  final MessageService _msgService;
  List<Message> messages;
  User user;

  ProfileComponent(this._usrService, this._msgService);

  Future<Null> ngOnInit() async {
    loadUser();
    loadMessages();
  }

  Future<Null> loadUser() async {
    try {
      user = await this._usrService.getUser();
      print("[Info] User loaded");
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  Future<Null> loadMessages() async {
    try {
      messages = (await this._msgService.getAll()).toList();
      print("[Info] Messages loaded");
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  dynamic _handleException(dynamic ex) {
		print("ProfileComponent");
		print(ex);  
	}
}