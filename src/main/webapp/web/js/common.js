/***
 * 用于在数组中查找是否存在
 * @param obj
 * @returns {boolean}
 */
Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}