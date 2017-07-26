import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import 'services/message/MessageService.dart';
import 'components/message/MessageComponent.dart';
import 'components/message/MessageDetailComponent.dart';
import 'services/category/CategoryService.dart';
import 'components/category/CategoryComponent.dart';
import 'components/login/LoginComponent.dart';
import 'components/register/RegisterComponent.dart';
import 'components/create/CreateComponent.dart';


@Component(
	selector: 'blackbox',
	templateUrl: 'AppComponent.html',
	styleUrls: const['AppComponent.css'],
	directives: const [ROUTER_DIRECTIVES, MessageComponent, CategoryComponent, LoginComponent, RegisterComponent, CreateComponent],
	providers: const [MessageService, CategoryService, ROUTER_PROVIDERS])
@RouteConfig(const [
	const Route(path: '', name: 'CreateMessage', component: CreateComponent, useAsDefault: true),
	const Route(path: '/messages', name: 'Messages', component: MessageComponent),
	const Route(path: '/categories', name: 'Categories', component: CategoryComponent),
	const Route(path: '/messages/:id', name: 'MessageDetail', component: MessageDetailComponent),
	const Route(path: '/register', name: 'Register', component: RegisterComponent)
])

class AppComponent	{
	String title = 'Schwarzes Brett';
}