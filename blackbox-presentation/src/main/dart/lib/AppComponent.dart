import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';

import 'home/HomeComponent.dart';
import 'login/LoginFormComponent.dart';
import 'message/MessageComponent.dart';
import 'profile/ProfileComponent.dart';

@Component(
  selector: 'blackbox',
  styleUrls: const ['AppComponent.css'],
  templateUrl: 'AppComponent.html',
  directives: const [ROUTER_DIRECTIVES, LoginFormComponent],
  providers: const [ROUTER_PROVIDERS],
)
@RouteConfig(const [
  const Route(path: '/', name: 'Home', component: HomeComponent, useAsDefault: true),
  const Route(path: '/message', name: 'Messages', component: MessageComponent),
  const Route(path: '/profile', name: 'Profile', component: ProfileComponent)
])
class AppComponent {
}