scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/customScreen/sort/SortLineDetails"], function (
    _dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnSortLineDetails) {
    return _dojodeclare("extn.customScreen.sort.SortLineDetailsBehaviorController", [_scServerDataController], {
        screenId: 'extn.customScreen.sort.SortLineDetails',
        mashupRefs: [{

            mashupId: 'extn_getItemDetailsForPick'
            ,
            mashupRefId: 'extn_getItemDetails'

        }]
    });
});