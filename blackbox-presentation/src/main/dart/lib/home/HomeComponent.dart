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
		loggedIn = await this._usrService.getUser() != null;
	}

  bool isLoggedIn() => loggedIn;

  void loginChanged(User user) {
    loggedIn = true;
    print("loggedIn true");
    print(user.toString());
  }

  void registerChanged(User user) {
    print("registered true");
    print(user.toString());
  }
}