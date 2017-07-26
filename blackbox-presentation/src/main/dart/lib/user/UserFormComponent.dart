import 'package:angular2/angular2.dart';

import '../_models/UserModel.dart';

@Component(
  selector: 'user-form',
  templateUrl: 'UserFormComponent.html',
  directives: const [COMMON_DIRECTIVES],
)
class UserFormComponent {
  @Input('user')
  User user;
  @Input('mode')
  String mode;
}