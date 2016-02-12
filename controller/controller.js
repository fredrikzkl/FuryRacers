var game = new Phaser.Game(800, 600, Phaser.AUTO, 'game', {
    preload: preload,
    create: create,
    update: update
});

var IP;

var GREY = "0xbebbbd";
var BLACK = "0x111213";
var WHITE = "0xffffff";
var RED = "0xD40100";

var whitePaddle;
var arrowLeft;
var arrowRight;
var whiteUnderRedButton;
var redButton;

var spriteRedButton;
var spriteArrowLeft;
var spriteArrowRight;

var buttonDown = function(buttonID){};
var buttonUp = function(buttonID){};

function preload() {


     addMessageHandler(function(msg){
        if(msg == "identified"){

            buttonDown = function(buttonID) {
              console.log("Down");
              sendToGame("buttonDown", buttonID);
            }

            buttonUp = function(buttonID){
              console.log("up");
              sendToGame("buttonUp", buttonID);
            }

            sendToBackend("get ip");
        }
    });
}

function create(){
    game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
    game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;
    game.scale.refresh();

    game.stage.backgroundColor = BLACK;


     createControllerLayout();

    game.input.onDown.add(function(pointer) {    //Reagerer på all trykk ned(onDown) på game området ==> game.input
      var data;

      if (pointer.targetObject) {                // Hvis trykk skjer på en knapp ==> pointer.targetObject == true;
        data = pointer.targetObject.sprite.data; // Henter data(knappID/buttonID) fra knappen som har blitt trykket.
        buttonDown(data);
      }

      target = pointer.targetObject;
    }, this);

    game.input.onUp.add(function(pointer) {
      var data;

      if (pointer.targetObject) {
        data = pointer.targetObject.sprite.data;
        buttonUp(data);
      }
    }, this);

}



function createControllerLayout(){
    //Den hvite bakgrunnen under pilene
    whitePaddle = game.add.graphics(0,0);
    whitePaddle.beginFill(WHITE, 1);
    whitePaddle.drawRect(
      game.stage.width/20,
      game.stage.height/2,
      game.stage.width/2,
      game.stage.height/4);
    //Pilene som skal ligge over det hvite området
    arrowLeft = game.add.graphics(0,0);
    arrowLeft.beginFill(BLACK,1);
    arrowLeft.drawRect(
      game.stage.width/18,
      game.stage.height/1.96,
      game.stage.width/2/2.1,
      game.stage.height/4.35);

    arrowRight = game.add.graphics(0,0);
    arrowRight.beginFill(BLACK,1);
    arrowRight.drawRect(
      game.stage.width/3.3,
      game.stage.height/1.96,
      game.stage.width/2/2.1,
      game.stage.height/4.35);
    //Grå området under knappen
    whiteUnderRedButton = game.add.graphics(0,0);
    whiteUnderRedButton.beginFill(GREY,1);
    whiteUnderRedButton.drawRect(
      game.stage.width/1.5, //Posisjon x
      game.stage.height/2.2, //Posisjon y
      game.stage.width/4, //Størrelse X
      game.stage.height/3); //Størrelse Y

    redButton = game.add.graphics(0, 0);
    redButton.beginFill(RED, 1);
    redButton.drawCircle(game.stage.width/1.265, //Posisjon x
    game.stage.height/1.6, //Posisjon y
    game.stage.width/4.3);

    addInput();
}

function addInput(){

    spriteRedButton = game.add.sprite(0, 0);
    spriteRedButton.addChild(redButton);
    spriteRedButton.data = 1;  //knappID/buttonID
    spriteRedButton.inputEnabled = true;

    spriteArrowRight = game.add.sprite(0, 0);
    spriteArrowRight.addChild(arrowRight);
    spriteArrowRight.data = 2; // knappID/buttonID
    spriteArrowRight.inputEnabled = true;

    spriteArrowLeft = game.add.sprite(0, 0);
    spriteArrowLeft.addChild(arrowLeft);
    spriteArrowLeft.data = 3; //knappID/buttonID
    spriteArrowLeft.inputEnabled = true;
}

function update(){

}
