ouzo-phpstorm-plugin
====================

Ouzo plugin for phpstorm

https://plugins.jetbrains.com/plugin/7565

Features:

  * Completion for array keys in constructor and create, createNoValidation, newInstance, assignAttributes and updateAttributes Model's methods based on Models @property tags
  * Translation extraction action for twig
  * Translation extraction action
  * References for translation keys
  * References for controllers and actions in routes
  * References for views
  * References for partials
  * References for models in relation definition
  * Navigation from Controllers actions to corresponding views (with 'Go To/Ouzo View' and 'Navigate/Related symbol' (Ctrl+Alt+Home))
  * Navigation from views to Controllers actions (with 'Navigate/Related symbol' (Ctrl+Alt+Home))
  * Annotate unused translations
  * Annotate invalid entries in translation files
  * Annotate missing translations
  * Add missing translation intention action
  * Edit translation intention action
  * Show all usages of a translation key
  * Remove unused translation intention action
  * Rename refactoring for translation keys
  * Translation key completion
   
  
You should add @property tags in php docs of Model classes. The plugin will complete array keys in constructor and create, createNoValidation, newInstance, assignAttributes and updateAttributes Model's methods.

```php
/**
 * @property string $login
 * @property string $password
 *
 * @package Model
 */
class User extends Model
{
    function __construct($attributes = array())
    {
        parent::__construct(array(
            'attributes' => $attributes,
            'fields' => array('login', 'password')));
    }
    ...
}

User::create(['login' => 'Bob', '|' ]);
//                               | ctrl+space will show supported attributes
    
    
    
```



