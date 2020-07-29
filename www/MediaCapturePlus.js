var exec = require('cordova/exec');

exports.coolMethod = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'coolMethod');
};

exports.testOpenCamera = function (success, error) {
    exec(success, error, 'MediaCapturePlus', 'testOpenCamera');
};