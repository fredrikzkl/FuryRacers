var game = new Phaser.Game(800, 600, Phaser.AUTO, 'game', {
    preload: preload,
    create: create,
    update: update
});

var accelerateButton;

var target;

var GREY = "0xbebbbd";
var BLACK = "0x111213";
var WHITE = "0xffffff";
var RED = "0xD40100";

var accelerate = function(button){};

function preload() {
    addMessageHandler(function(msg){
        if(msg == "identified"){

            accelerate = function(button) {
              console.log("Hell to life");
              sendToGame("accelerate", button);
            }

        }
    })

}

function create(){
    game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
    game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;
    game.scale.refresh();

    game.stage.backgroundColor = BLACK;

    game.input.onDown.add(function(pointer) {
        var data;


        if (pointer.targetObject) {
            data = pointer.targetObject.sprite.data;
        }

        if (!game.scale.isFullScreen) {
            game.scale.startFullScreen(false);
        }else if (pointer.targetObject) {
            accelerate(pointer.targetObject.sprite.data);
        }

        target = pointer.targetObject;

    }, this);

    createControllerLayout();


}

function createControllerLayout(){
    //Den hvite bakgrunnen under pilene
    var whitePaddle = game.add.graphics(0,0);
    whitePaddle.beginFill(WHITE, 1);
    whitePaddle.drawRect(
      game.stage.width/20,
      game.stage.height/2,
      game.stage.width/2,
      game.stage.height/4);
    //Pilene som skal ligge over det hvite området
    var arrowLeft = game.add.graphics(0,0);
    arrowLeft.beginFill(BLACK,1);
    arrowLeft.drawRect(
      game.stage.width/18,
      game.stage.height/1.96,
      game.stage.width/2/2.1,
      game.stage.height/4.35);
    var arrowRight = game.add.graphics(0,0);
    arrowRight.beginFill(BLACK,1);
    arrowRight.drawRect(
      game.stage.width/3.3,
      game.stage.height/1.96,
      game.stage.width/2/2.1,
      game.stage.height/4.35);
    //Grå området under knappen
    var buttonUnder = game.add.graphics(0,0);
    buttonUnder.beginFill(GREY,1);
    buttonUnder.drawRect(
      game.stage.width/1.5, //Posisjon x
      game.stage.height/2.2, //Posisjon y
      game.stage.width/4, //Størrelse X
      game.stage.height/3); //Størrelse Y
    circleButton = game.add.graphics (0,0);
    circleButton.beginFill(RED,1);
    circleButton.drawCircle(
      game.stage.width/1.265, //Posisjon x
      game.stage.height/1.6, //Posisjon y
      game.stage.width/4.3); //Radius

    accelerateButton = game.add.sprite(0,32);
    circleButton.addChild(accelerateButton);

    addInput();
}

function addInput(){
    g = game.add.graphics(0, 0);
    g.beginFill(RED, 1);
    g.drawCircle(game.stage.width/1.265, //Posisjon x
    game.stage.height/1.6, //Posisjon y
    game.stage.width/4.3);

    s = game.add.sprite(0, 0);
    s.addChild(g);
    s.data = 3;
    s.inputEnabled = true;
    accelerateButton.inputEnabled = true;
    circleButton.inputEnabled = true;
    accelerateButton.data = "accelerate";
    circleButton.data = "accelerate";

}

function update() {


  if(game.scale.isFullScreen){

    game.input.onDown.add(function(pointer){
      var data;


      if (pointer.targetObject) {
          data = pointer.targetObject.sprite.data;
      }

      if(data == "accelerate"){
        console.log("Skyt meg i hodet");
      }

    })

  }
}
