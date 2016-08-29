# 关于NSUSerDefaults的使用感悟

标签（）： iOS NSUSerDefaults

---

## NSUSerDefaults存取UISwitch的使用
今天需要进行项目里游戏音效和震动提示的打开与关闭，因此用到了UISwitch控件，通过其事件来完成功能。但是在软件重启的情况下，因为设置无法保存，所以上次设置失效。因此选用NSUSerDefaults来保存数据。
**NSUSerDefaults适合存储轻量级的本次数据，一些简单的数据（NSString）密码，网址，登陆的状态，以及本文里说到的开关状态。**


----------
###NSUserDefaults可以存储的类型
NSUserDefaults是一个单例， 在程序中只有一个实例对象，用于数据的保存。其支持的数据类型有：`NSNumber, NSString, NSData, NSArray, NSDictionary, BOOL`。**注意：存储对象不可变**，其使用方法非常简单，下面是今天用到的代码：
```
- (void)onSwitchChange:(id)sender
{
    BOOL isOn = self.gameSwitch.isOn;
    if (self.gameTag == Game_Audio) {
        [[NSUserDefaults object] setBool:isOn forKey:KC_SettingGameAudio];
    } else {
        [[NSUserDefaults object] setBool:isOn forKey:KC_SettingGameVibrate];
    }
    if ([self.delegate respondsToSelector:@selector(didClickWith:withTag:)]) {
        [self.delegate didClickWith:self.gameSwitch.isOn withTag:(int)self.gameTag];
    }
}
```
上面代码中，在UISwitch点击事件发生的情况下，通过`setBool： forkey:（唯一字符串）`将开关的状态进行存储（这里分了音效还有震动）。其余类型的存储也是一样。
```
-(void)setGameSwitchWithRow:(NSInteger)row
{
    switch (row) {
        case 0:
            self.gameLabel.text = @"游戏音效";
            self.gameTag = Game_Audio;
            [self setOn:[[DSetting setting] boolForKey:KC_SettingGameAudio]];
            break;
            
        case 1:
            self.gameLabel.text = @"震动提示";
            self.gameTag = Game_Vibration;
            [self setOn:[[DSetting setting] boolForKey:KC_SettingGameVibrate]];
            break;
        default:
            break;
    }
}
```
在控件初始化的情况下，在通过`[object boolForKey:(NSString *)]`进行读取设置。
最后根据开关的状态，针对不同的内容进行设置。
```
- (void)didClickWith:(BOOL)isOn withTag:(int)tag
{
    if (tag == Game_Audio) {
        //游戏是否静音
        [[GameAudioManager sharedInstance] muteAudio:isOn];
    } else if (tag == Game_Vibration){
        //是否开启游戏振动
        [[GameAudioManager sharedInstance] enableVibe:isOn];
    }
}
```

## NSUserDefaults存储自定义数据
### 将自定义类型存储为NSData类型。
例如想要存储多个人的各项信息，比如姓名，电话和性别。大量数据还是用SQLite存储。因为NSUSerDefaults不支持这样存储，可以转换为NSDdata类型。
比如Person类有三个属性，name，phoneNumber，sex，对其进行归档。
需要在头文件声明`NSCoding协议`, 在m文件实现encodeWithCode和initWithCode即可。
`.h:  @interface Person:NSObject <NSCoding>`
```.m:
- (void) encodeWithCode: (NSCoder *)aCoder
{
[aCode encodeObject:self.name forkey:@"name"];
...
}
- (id) initWithCode: (NSCoder) aDecoder
{
    if(self = [super init])
    {
    self.name = [aDecode decodeObjectForKey:@"name"];
    ...
    }
    return self;
}
```
然后将可以将自定义的数据转为NSData了。

### 对NSData进行存储。
对多人数据的存储，可以新建一个NSMutableArray来存储，这里就不再说明。
对person实例赋值后，可以通过以下方法来存取使用。
```
// save
NSData *data = [NSKeyedArchiver archiverDataWithRootObject:person];
NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
[user setObject:data forKey:@"person"]

//read
NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
NSData *data = [user objectForKey:@"person"]
Person *person = [NSKeyedUnarchiver unarchiveObjectWithData:data];
```

总之，NSUserDefaults用于永久保存程序中一些数据，是最简单也是最方便的。以后使用SQLite会介绍其和FMDB的内容。





