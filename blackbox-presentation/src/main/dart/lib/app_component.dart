import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import 'services/message/MessageService.dart';
import 'components/message/MessageComponent.dart';
import 'components/message/MessageDetailComponent.dart';
import 'services/category/CategoryService.dart';
import 'components/category/CategoryComponent.dart';
import 'components/login/LoginComponent.dart';
import 'components/register/RegisterComponent.dart';


@Component(
	selector: 'blackbox',
	templateUrl: 'AppComponent.html',
	styleUrls: const['AppComponent.css'],
	directives: const [ROUTER_DIRECTIVES, MessageComponent, CategoryComponent, LoginComponent, RegisterComponent],
	providers: const [MessageService, CategoryService, ROUTER_PROVIDERS])
@RouteConfig(const [
	const Route(path: '/messages', name: 'Messages', component: MessageComponent),
	const Route(path: '/categories', name: 'Categories', component: CategoryComponent, useAsDefault: true),
	const Route(path: '/messages/:id', name: 'MessageDetail', component: MessageDetailComponent)
])

class AppComponent	{
	String title = 'Schwarzes Brett';
}