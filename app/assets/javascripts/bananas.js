

var stage = new Kinetic.Stage({
    container: 'canvasBlock',
    width: 600,
    height: 600
});


jQuery.ajax("http://localhost:9000/giveMeState", {
    success: function(data) {

        var tick = 40;
        var v = 90;
        var angleInRadians = 0;

        var turnRatio = 30;

        var x1 = 200;
        var y1 = 200;
        var x2 = x1;
        var y2 = y1;



        data.forEach(function(item) {

            switch(item.command) {
                case "start":
                    // layer.clear();
                    break;

                case "move":

                    switch (item.how) {

                    case "left":
                        v = v - turnRatio;
                        break;

                    case "right":
                        v = v + turnRatio;
                        break;

                    case "forward":
                        break;
                    }

                break;
            }
            if (v > 360) v = v - 360;
            if (v < 0) v = v + 360;

            angleInRadians = (v * (Math.PI / 180));

            // cos something
            y2 = y1 - Math.sin(angleInRadians) * tick;
            x2 = x1 + Math.cos(angleInRadians) * tick;

            console.log("command: " + item.command + "v: " + v + ", from: (" + x1 + ", " + y1 + "), to: (" + x2 + ", " + y2 + ")")
            var layer = new Kinetic.Layer();
            layer.add(new Kinetic.Line({
                points: [[x1, y1], [x2, y2]],
                stroke: 'black',
                strokeWidth: 2
            }));

            // add the layer to the stage
            stage.add(layer);

            // this is where we are in the next iteration
            x1 = x2;
            y1 = y2;
        });
    }
});

/*
v
*/