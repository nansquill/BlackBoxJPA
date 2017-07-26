import 'package:angular2/angular2.dart';

import '../_models/CategoryModel.dart';

@Component(
  selector: 'category-form',
  templateUrl: 'CategoryFormComponent.html',
  directives: const [COMMON_DIRECTIVES],
)
class CategoryFormComponent {
  @Input('category')
  Category category;
  @Input('mode')
  String mode;
}