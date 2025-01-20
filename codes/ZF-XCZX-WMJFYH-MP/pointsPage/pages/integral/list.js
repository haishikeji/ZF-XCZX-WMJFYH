// pointsPage/pages/integral/list.js
import { shopList, Haversine } from '../../../api/newApi.js';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        list: [],
        zuijinId: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        if (app.isWxLogin(3)) {
            this.getList();
        }
    },
    getGPS() {
        let vm = this;
        wx.getLocation({
            type: 'wgs84', // 返回坐标类型，可选值：'wgs84'、'gcj02'
            success: function (res) {
                var latitude = res.latitude; // 纬度
                var longitude = res.longitude; // 经度
                var speed = res.speed; // 速度，单位 m/s
                var accuracy = res.accuracy; // 精度，单位 m
                Haversine({
                    lat: latitude,
                    lon: longitude,
                    ...app.getCunId()
                }).then(r => {
                    if (r.code == 0) {
                        vm.setData({
                            zuijinId: r.data.id
                        })
                    }

                })
                console.log('Latitude: ' + latitude);
                console.log('Longitude: ' + longitude);
                console.log('Speed: ' + speed);
                console.log('Accuracy: ' + accuracy);

            },
            fail: function (err) {
                console.error('Failed to get location:', err);
            }
        });
    },
    getList() {
        let vm = this;
        shopList({ ...app.getCunId(), pageNo: 1, pageSize: 999 }).then(res => {
            if (res.code == 0) {
                vm.setData({
                    list: res.data.records
                })
            }
        })
    },
    daohang(e) {
        if (e.mark.item) {
            let gps = e.mark.item.shopCoordinate.split(',');
            wx.openLocation({
                latitude: Number(gps[1]), // 目标地点的纬度
                longitude: Number(gps[0]), // 目标地点的经度
                name: e.mark.item.shopName,
                address: e.mark.item.shopAddress,
                scale: 18, // 地图缩放级别
                success(res) {
                    // 打开成功的回调
                    console.log('打开成功', res);
                },
                fail(err) {
                    // 打开失败的回调
                    console.error('打开失败', err);
                }
            });
        }
    },
    godetail(e) {
        let vm = this;
        let pages = getCurrentPages(); // 页面对象
        console.log(pages)
        if (pages.length > 1) {
            const beforePage = pages[pages.length - 2];  //前一个页面
            if (beforePage.route == 'pointsPage/pages/integral/index') {
                beforePage.setData({
                    shopInfo: {
                        id: e.mark.item.id, page: 1,
                    },
                    page: 1,
                    hasmore: true,
                    shopList: []
                });
            }
            wx.navigateBack({ //跳转到前一个页面
                delta: 1,//前一个页面
            })

        } else {
            wx.redirectTo({
                url: './index?id=' + e.mark.item.id,
            })
        }

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
        if (!this.data.zuijinId) {
            this.getGPS();
        }
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