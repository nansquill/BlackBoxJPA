import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';

import '../_services/MessageService.dart';
import '../login/LoginFormComponent.dart';
import '../message/MessageFormComponent.dart';

@Component(
  selector: 'app-message',
  templateUrl: 'MessageComponent.html',
  providers: const[ROUTER_PROVIDERS, MessageService],
  directives: const[ROUTER_DIRECTIVES, MessageFormComponent]
)
class MessageComponent implements OnInit{
  final MessageService _msgService;
  List<Message> messages;

  MessageComponent(this._msgService);

  Future<Null> ngOnInit() async {
		loadMessages();
	}

  Future<Null> loadMessages() async {
    messages = (await this._msgService.getAll()).toList();
    print("[Info] Messages loaded");
  }

  Future<Null> deleteChanged(Message message) async {
      this._msgService.delete(message);
      await loadMessages();
  }

  Future<Null> updateChanged(Message message) async {
      this._msgService.update(message);
      await loadMessages();
  }

  Future<Null> createChanged(Message message) async {
      this._msgService.create(message);
      await loadMessages();
  }
}