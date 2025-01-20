// otherPage/pages/sunVillageAffairs/manageFramework/add.js

import {
    assetscateautoList,
    assetscontentAdd,
    assetscontentById,
    assetscontentEdit
} from '../../../../api/sunVillage'
const app = getApp()
import WxParse from '../../../../wxParse/wxParse';
Page({

    /**
     * 页面的初始数据
     */
    data: {

        customList: [],
        fileList: [],

        pid: '',
        cid: '',
        img: [],
        id: 0
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        let vm = this
        vm.setData({
            pid: options.pid,
            cid: options.cid,
            id: options.id || 0
        })
        vm.getCustom()
        if (options.id) {
            vm.getDetail()
        }
    },
    // 详情
    getDetail() {
        let that = this
        wx.showLoading({
            title: '加载中...',
            mask: true
        })
        assetscontentById(that.data.id).then(res => {
            wx.hideLoading({
                success: (res) => {},
            })
            if (res.code == 0) {

                if (res.data.file) {
                    let arr = JSON.parse(res.data.file)
                    arr.forEach(a => {
                        let index = a.label.lastIndexOf('.')
                        let index2 = a.label.length
                        var type = a.label.substr(index + 1, index2)
                        a.type = type
                    })
                    res.data.newFile = arr
                }
                that.setData({
                    name: res.data.name,
                    img: [res.data.content],
                    fileList: JSON.parse(res.data.file)
                })
            }
        })
    },
    //获取自定义列表
    getCustom() {
        let vm = this
        assetscateautoList({
            cid: vm.data.cid,
            state: 1
        }).then(res => {
            res.data.forEach(r => {
                if (r.type == 2) {
                    r.valList = r.val.split(',')
                }
            })
            vm.setData({
                customList: res.data
            })
        })
    },
    onRadioChange(e) {
        let item = e.currentTarget.dataset.item
        let vm = this
        vm.data.customList.forEach(c => {
            if (c.id == item.id) {
                c.value = e.detail
            }
        })
        vm.setData({
            customList: vm.data.customList
        })
    },
    bindCustom(e) {
        let item = e.currentTarget.dataset.item
        let vm = this
        vm.data.customList.forEach(c => {
            if (c.id == item.id) {
                c.value = e.detail.value
            }
        })
        vm.setData({
            customList: vm.data.customList
        })
    },
    // 提交
    formSubmit(e) {
        let that = this
        let arr = []

        let data = e.detail.value
        if (data.name == '') {
            wx.showToast({
                title: '请输入标题',
                icon: 'none'
            })
            return
        }
        if (that.data.img.length == 0) {
            wx.showToast({
                title: '请上传图片',
                icon: 'none'
            })
            return
        }
        that.data.customList.forEach(c => {
            if (c.isRequired == 1) {
                if (!c.value) {
                    if (c.type == 1) {
                        wx.showToast({
                            title: '请输入' + c.name,
                            icon: 'none'
                        })
                        return
                    } else if (c.type == 2) {
                        wx.showToast({
                            title: '请选择' + c.name,
                            icon: 'none'
                        })
                        return
                    }

                }
            }
        })
        that.data.customList.forEach(c => {
            let obj = {
                autoId: c.id,
                name: c.name,
                type: c.type,
                val: c.value
            }
            arr.push(obj)
        })

        let currentCun = wx.getStorageSync('currentCun')

        let obj = {
            id: that.data.id,
            name: data.name,
            content: that.data.img.join(','),
            did: currentCun.deptId,
            cid: that.data.cid,
            dsid: currentCun.groupId,
            file: JSON.stringify(that.data.fileList),
            assetsContentAutoList: arr
        }
        if (that.data.id == 0) {
            assetscontentAdd(obj).then(res => {
                console.log(res.data);
                wx.redirectTo({
                    url: './index?pid=' + that.data.pid
                })
            })
        } else {
            assetscontentEdit(obj).then(res => {
                console.log(res.data);
                wx.redirectTo({
                    url: './index?pid=' + that.data.pid
                })
            })
        }


    },

    // 上传附件
    uploadClick() {
        var that = this
        var arr = []
        var token = wx.getStorageSync('token')
        wx.chooseMessageFile({
            count: 1,
            type: 'file',
            extension: ['doc', 'docx', 'xls', 'xlsx', 'jpg', 'png', 'jpeg', 'pdf'],
            success(res) {
                wx.showLoading({
                    title: '正在上传中...',
                    mask: true
                });

                const tempFilePaths = res.tempFiles;
                const allowedExtensions = ['doc', 'docx', 'xls', 'xlsx', 'jpg', 'png', 'jpeg', 'pdf'];

                for (let i = 0; i < tempFilePaths.length; i++) {
                    const file = tempFilePaths[i];
                    const fileExtension = file.path.split('.').pop().toLowerCase();

                    if (allowedExtensions.includes(fileExtension)) {
                        wx.uploadFile({
                            url: app.globalData.url + '/api/money/record/file',
                            filePath: file.path,
                            name: 'file',
                            formData: {
                                'file': 'file'
                            },
                            header: {
                                "Content-Type": "application/x-www-form-urlencoded",
                                "Authorization": token
                            },
                            success: function (resp) {
                                wx.hideLoading({
                                    success: (res) => {}
                                });
                                var data = JSON.parse(resp.data);
                                arr.push({
                                    label: data.data.kname,
                                    value: data.data.kpath
                                });
                                that.setData({
                                    fileList: that.data.fileList.concat(arr)
                                });
                            },
                            fail: function (err) {
                                wx.hideLoading({
                                    success: (res) => {}
                                });
                                console.log(err);
                            }
                        });
                    } else {
                        wx.hideLoading({
                            success: (res) => {}
                        });
                        wx.showToast({
                            title: `文件 ${file.name} 类型不支持`,
                            icon: 'none',
                            duration: 2000
                        });
                    }
                }


            },
            fail(err) {
                console.log('err', err);
            }
        });
    },
    // 删除附件
    delClick(e) {
        let index = e.currentTarget.dataset.index
        let arr = this.data.fileList.splice(index, 1)
        this.setData({
            fileList: this.data.fileList
        })
    },

    onChangeTap(e) {
        wx.showLoading({
            title: '图片上传中...',
        })
        var count = 0
        var arr = []
        var token = wx.getStorageSync('token')
        var that = this
        for (var i = 0; i < e.detail.all.length; i++) {
            wx.uploadFile({
                url: app.globalData.url + '/api/money/record/file',
                filePath: e.detail.all[i],
                name: 'file',
                formData: {
                    'file': 'file'
                },

                header: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Authorization": token
                },
                success(res) {
                    var data = JSON.parse(res.data);
                    console.log(data, 'data');
                    arr.push(data.data.kpath)
                    that.setData({
                        img: arr
                    })
                    count++
                    if (count >= e.detail.all.length) {
                        wx.hideLoading()
                    }
                }
            })
        }

    },
    onRemoveTap(e) {
        var arr = this.data.img
        arr.splice(e.detail.index, 1)
        this.setData({
            img: arr
        })
    },
})