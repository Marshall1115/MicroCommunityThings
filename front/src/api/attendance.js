import request from '@/utils/request'

export function getAttendances(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
    }
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9997',
            communityId:communityId
        }
    })
}

export function getAttendancesByCondition(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteAttendances(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}

export function restartAttendances(params) {
    return request({
        url: '/api/machine/startMachine',
        method: 'post',
        data:params
    })
}

export function getAttendanceFace(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getMachineFaces',
        method: 'get',
        params
    })
}

export function getAttendanceClasses(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/attendance/getClasses',
        method: 'get',
        params: {
            page: 1,
            row: 10
        }
    })
}

export function getClasses(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/attendance/getClasses',
        method: 'get',
        params
    })
}

export function getAttendanceClassesAttr(classId) {
    return request({
        url: '/api/attendance/getClassesAttrs',
        method: 'get',
        params: {
            classId: classId
        }
    })
}


export function getDepartment(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/attendance/getDepartments',
        method: 'get',
        params
    })
}

export function getStaffs(params) {
   
    return request({
        url: '/api/attendance/getStaffs',
        method: 'get',
        params
    })
}


export function saveClassesStaffs(params) {
    return request({
        url: '/api/attendance/saveClassesStaffs',
        method: 'post',
        data:params
    })
}

export function getClassesStaffs(params) {
    return request({
        url: '/api/attendance/getClassesStaffs',
        method: 'get',
        params
    })
}

export function deleteClassesStaff(params) {
    return request({
        url: '/api/attendance/deleteClassesStaff',
        method: 'post',
        data:params
    })
}


export function getAttendanceTasks(params) {
    return request({
        url: '/api/attendance/getAttendanceTasks',
        method: 'get',
        params
    })
}

export function getMonthAttendance(params) {
    return request({
        url: '/api/attendance/getMonthAttendance',
        method: 'get',
        params
    })
}

export function updateAttClass(params) {
    return request({
        url: '/api/attendance/updateAttClass',
        method: 'post',
        data:params
    })
  }
  
  export function insertAttClass(params) {
    return request({
        url: '/api/attendance/insertAttClass',
        method: 'post',
        data:params
    })
  }

  export function deleteAttClassControls(params) {
    return request({
        url: '/api/attendance/deleteAttClass',
        method: 'post',
        data:params
    })
  }

  export function getStaffAttendanceLog(params) {
    return request({
        url: '/api/attendance/getStaffAttendanceLog',
        method: 'get',
        params
    })
}
  