import 'dart:async';

import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';
import 'package:angular2/platform/common.dart';

import 'package:blackbox/models/Message.dart';
import 'package:blackbox/services/message/MessageService.dart';

@Component(
	selector: 'message-detail',
	templateUrl: 'MessageDetailComponent.html',
	styleUrls: const['MessageDetailComponent.css'],
	directives: const [COMMON_DIRECTIVES]
)
class MessageDetailComponent implements OnInit {
	Message message;
	
	final MessageService _messageService;
	final RouteParams _routeParams;
	final Location _location;
	
	MessageDetailComponent(this._messageService, this._routeParams, this._location);
	
	Future<Null> ngOnInit() async {
		var _id = _routeParams.get('id');
		var id = int.parse(_id ?? '', onError: (_) => null);
		if (id != null) message = await(_messageService.getMessage(id));
	}
	
	void goBack() => _location.back();	
}