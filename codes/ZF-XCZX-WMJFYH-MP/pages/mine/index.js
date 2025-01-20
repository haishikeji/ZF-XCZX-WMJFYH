// pages/mine/index.js
import {
    getmanuser,
    currentVillage,
    myRole,
    villagesDate
} from '../../api/newApi.js';
import {
    wxuser, login
} from '../../api/api.js';
const app = getApp();

Page({

    /**
     * 页面的初始数据
     */
    data: {
        codePopShow: false,
        userInfo: null,
        cunganbuFlag: false,
        shanghuFlag: false,
        cunminFlag: false,

        role: wx.getStorageSync('role') || 0,
        fontMultiple: app.setFontMultiple()

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        let vm = this;
        this.setData({
            fontMultiple: app.setFontMultiple(),
            role: wx.getStorageSync('role') || 0,
            lang: app.getLang()
        })
        if (app.isWxLogin(3)) {
            let userInfo = wx.getStorageSync('userInfo')
            let currentCun = wx.getStorageSync('currentCun')
            let role = wx.getStorageSync('role')
            if (role != 0) {
                getmanuser({ idcard: userInfo.idcard, type: role }).then(r2 => {
                    if (r2.code == 0) {
                        if (r2.data.ztype == 1 && role == 4) {
                            r2.data.role = 4;
                        }
                        wx.setStorageSync('userInfo', r2.data);
                        vm.setData({
                            userInfo: r2.data,
                            phone: app.maskPhoneNumber(r2.data.phone),
                            role
                        })
                        let cdid = r2.data.cdid;
                        role == 4 ? cdid = r2.data.zcdid : '';
                        if (role != 0 && cdid != currentCun.deptId) {
                            wx.showToast({
                                icon: 'none',
                                mask: true,
                                title: '您绑定的村已更改，需要重新选择',
                                duration: 2000
                            })
                            setTimeout(() => {
                                wx.navigateTo({
                                    url: role == 4 ? '/volunteer/pages/setAddress/index' : '/pages/login/setAddress',
                                })
                                return false
                            }, 1500)
                        }
                    } else {
                        wx.showModal({
                            title: '提示',
                            showCancel: false,
                            content: '获取信息失败，请重新登录',
                            complete: (res) => {
                                if (res.cancel) {

                                }

                                if (res.confirm) {
                                    wx.removeStorageSync('wxUserInfo');
                                    wx.removeStorageSync('role');
                                    wx.removeStorageSync('token');
                                    wx.removeStorageSync('userInfo');
                                    wx.removeStorageSync('currentCun');
                                    wx.reLaunch({
                                        url: '/pages/login/login',
                                    })
                                }
                            }
                        })

                    }
                })

                if (role == 1 || role == 4) {
                    currentVillage().then(r2 => {
                        if (r2.code == 0) {
                            vm.setData({
                                jifen: r2.data
                            })

                        }
                    })
                }

                myRole().then(r3 => {
                    if (r3.code == 0) {
                        r3.data.forEach(i => {
                            if (i.loginType == 2 && i.btype != 1) {
                                vm.setData({
                                    cunganbuFlag: true
                                })
                            }
                            if (i.loginType == 3) {
                                vm.setData({
                                    shanghuFlag: true
                                })
                            }
                            if (i.loginType == 1) {
                                vm.setData({
                                    cunminFlag: true
                                })
                            }
                        })
                    }
                })

            }
        }
    },



    goPage(e) {
        console.log(e.mark)
        wx.navigateTo({
            url: e.mark.url,
        })
    },
    goPage2(e) {

        let vm = this;
        wx.showModal({
            title: '提示',
            content: '您将以' + (e.mark.type == 2 ? '干部' : '') + (e.mark.type == 3 ? '商家' : '') + '的身份使用小程序',
            complete: (res) => {
                if (res.cancel) {
                }
                if (res.confirm) {
                    login({ phone: vm.data.userInfo.phone, type: e.mark.type }).then(r2 => {
                        if (r2.code == 0) {
                            wx.setStorageSync('role', e.mark.type)
                            wx.setStorageSync('token', r2.data.token)
                            wx.setStorageSync('userInfo', r2.data.manUser)
                            if (e.mark.type == 2) {
                                if (vm.data.role == 2 && vm.data.userInfo.btype == 3) {
                                    wx.reLaunch({
                                        url: '/otherPage/pages/zhenDataAnalysis/index',
                                    })
                                } else {
                                    wx.reLaunch({
                                        url: e.mark.url,
                                    })

                                }
                            }

                            if (e.mark.type == 3) {
                                if (r2.data.manUser.cdid) {
                                    villagesDate({ id: r2.data.manUser.cdid }).then(r5 => {
                                        wx.hideLoading()
                                        if (r5.code == 0) {
                                            wx.setStorageSync('currentCun', r5.data);
                                            wx.reLaunch({
                                                url: '/otherPage/pages/merchant/index',
                                            })
                                        } else {
                                            wx.showToast({
                                                icon: 'none',
                                                title: r5.msg,
                                            })
                                        }
                                    }).catch(err5 => {
                                        wx.hideLoading()
                                        wx.showToast({
                                            icon: 'none',
                                            title: err5.msg,
                                        })
                                    })

                                }

                            }

                        }
                    })
                }
            }
        })

    },

    codePopOpen() {
        this.setData({
            codePopShow: true
        })
    },

    codePopHide() {
        this.setData({
            codePopShow: false
        })
    },

    onChooseAvatar(e) {
        let vm = this;
        const { avatarUrl } = e.detail
        console.log(e.detail)
        wx.showLoading({
            title: '请稍候',
        })
        wx.uploadFile({
            url: app.globalData.url + '/api/fastfile/upload/szrj-1256675456',
            filePath: avatarUrl,
            name: 'file',
            formData: {
                'file': 'file'
            },

            header: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": wx.getStorageSync('token')
            },
            success(res) {
                console.log(res)
                wxuser({
                    avatarUrl: JSON.parse(res.data).data.kpath
                }).then(r => {
                    wx.hideLoading()
                    if (r.code == 0) {
                        vm.setData({
                            ['userInfo.avatar']: r.data.avatar
                        })

                        wx.showToast({
                            title: '更换成功',
                        })
                        wx.setStorageSync('userInfo', vm.data.userInfo);
                    } else {
                        wx.showToast({
                            icon: 'none',
                            title: '更换失败,请稍候重试',
                        })
                    }

                }).catch(err => {
                    wx.showToast({
                        icon: 'none',
                        title: '更换失败,请稍候重试',
                    })
                })

            }
        })
    },

    tuichu() {
        wx.showModal({
            title: '提示',
            content: this.data.role != 0 ? '确定要退出登录吗？' : '确定要退出游客模式吗？',
            complete: (res) => {
                if (res.cancel) {

                }

                if (res.confirm) {
                    wx.removeStorageSync('wxUserInfo');
                    wx.removeStorageSync('role');
                    wx.removeStorageSync('token');
                    wx.removeStorageSync('userInfo');
                    wx.removeStorageSync('currentCun');
                    wx.reLaunch({
                        url: '/pages/login/login',
                    })
                }
            }
        })
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})