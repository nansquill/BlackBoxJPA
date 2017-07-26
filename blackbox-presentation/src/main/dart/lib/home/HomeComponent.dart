import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';

import '../_models/UserModel.dart';
import '../_services/UserService.dart';
import '../login/LoginFormComponent.dart';

@Component(
  selector: 'app-home',
  templateUrl: 'HomeComponent.html',
  providers: const[ROUTER_PROVIDERS, UserService],
  directives: const[ROUTER_DIRECTIVES, LoginFormComponent]
)
class HomeComponent implements OnInit{
  final UserService _usrService;
  bool loggedIn = false;

  HomeComponent(this._usrService);

  Future<Null> ngOnInit() async {
    try {
		  loggedIn = await this._usrService.getUser() != null;
    }
    catch(ex) {
      _handleException(ex);
    }
	}

  bool isLoggedIn() => loggedIn;

  void loginChanged(User user) {
    try {
      loggedIn = true;
      print("loggedIn true");
      print(user.toString());
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  void registerChanged(User user) {
    try {
      print("registered true");
      print(user.toString());
    }
    catch(ex) {
      _handleException(ex);
    }
  }

  dynamic _handleException(dynamic ex) {
    print("HomeComponent");
    print(ex);  
  }
}