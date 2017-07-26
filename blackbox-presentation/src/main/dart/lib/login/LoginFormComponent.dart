import 'dart:async';
import 'dart:convert';
import 'dart:html';

import 'package:angular2/angular2.dart';

import '../_models/UserModel.dart';
import '../_services/UserService.dart';

@Component(
	selector: 'login-form',
	templateUrl: 'LoginFormComponent.html',
	providers: const [UserService],
	directives: const [COMMON_DIRECTIVES]
)
class LoginFormComponent implements OnInit {
	@Input('currentUser')
	User currentUser;
	@Input('mode')
	String mode;
	final _changeRequest = new StreamController<User>();
	@Output()
	Stream<User> get changedRequest => _changeRequest.stream;

	final UserService _usrService;
	bool loggedIn = false;

	LoginFormComponent(this._usrService);

	Future<Null> ngOnInit() async {
		if(!currentUser) {
			currentUser = this._usrService.getUser();
			if(currentUser)	{
				print("[Info] Found user");
				_changeRequest.add(currentUser);
			}
		}
		else
		{
			await this.getLogin();
		}
	}

	dynamic getLogin() async {
		if(mode != "login") {
			print("[Error] You are not allowed to login here");
			return false;
		}
		await this._usrService.login(currentUser);
		currentUser = await this._usrService.getUser();
		if(currentUser != null)	{
			loggedIn = true;
			_changeRequest.add(currentUser);
			print("[Info] User " + currentUser.username + " has benn logged in");
			return true;
		}
		print ("[Error] User is not logged in");
		return false;
	}

	dynamic getLogout() async {
		if(mode != "login")	{
			print("[Error] You are not allowed to logout here");
			return false;
		}
		User resutl = await this._usrService.logout();
		if(result != null) {
			loggedIn = false;
			print("[Info] User " + result.username + " has been logged out");
			return true;
		}
		print("[Info] User is not logged out");
		return false;
	}

	dynamic getRegister() async {
		if(mode != "register") {
			print("[Error] You are not allowed to register here");
			return false;
		}
		User result = await this._usrService.register(currentUser);
		if(currentUser != null)	{
			_changeRequest.add(currentUser);
			print("[Info] User " + result.username + " has been registered");
			return true;
		}	
		print("[Info] User is not registered");
		return false;
	}

	bool isLoggedIn() => loggedIn;
}
