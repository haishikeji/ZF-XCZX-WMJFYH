// otherPage/pages/jinrongchaoshi/list.js
import { readSzFinance } from './../../../api/newApi'
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        cate: '',
        d: [],
        // 分页
        page: 1,
        size: 20,
        pages: 0,
        current: 0,
        total: 0,
        noData: false,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        if (options.cate) {
            this.setData({
                cate: options.cate,
                name: options.name
            })
        }
        this.init();

    },
    async init() {
        let t = this;
        t.setData({
            page: 1,
            pages: 0,
            current: 0,
            noData: false,
            kong: false
        })
        t.getMore();
    },
    extractTextFromHTML(html) {
        const regex = /<[^>]*>/g; // 匹配所有 HTML 标签
        const extractedText = html.replace(regex, ''); // 去除标签
        return extractedText.trim(); // 去除首尾空格
    },
    getMore() {
        let t = this;
        var promise = new Promise((resolve, reject) => {
            if (t.data.pages >= t.data.current) {
                readSzFinance({
                    pageNo: t.data.page,
                    pageSize: t.data.size,
                    cate: t.data.cate
                }).then(r => {
                    if (r.httpCode == 200 || r.code == 0) {
                        let d;
                        if (t.data.page == 1) {
                            d = [];
                        } else {
                            d = t.data.d;
                        }
                        if (r.data.records.length) {
                            r.data.records.forEach(i => {
                                i.createTime = i.createTime ? i.createTime.substring(0, 10) : '';
                                if (i.sortcontent) {
                                    i.sortcontent = t.extractTextFromHTML(i.sortcontent);
                                }
                                d.push(i)
                            })
                            if (r.data.records.length < r.data.size) {
                                t.setData({
                                    noData: true,
                                })
                            } else {
                                t.setData({
                                    noData: false,
                                })
                            }
                            t.setData({
                                kong: false,
                            })
                        } else {
                            t.setData({
                                noData: true,
                            })
                            if (t.data.page == 1) {
                                t.setData({
                                    kong: true,
                                })
                            }
                        }
                        t.setData({
                            d,
                            page: ++t.data.page,
                            pages: r.data.pages,
                            current: r.data.current,
                            total: r.data.total
                        })
                    }

                    resolve();
                })
            } else {
                t.setData({
                    noData: true
                })
                resolve();
            }
        });
        return promise;
    },
    goDetail(e) {
        wx.navigateTo({
            url: './detail?id=' + e.mark.item.id,
        })
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
        if (!this.data.noData) {
            this.getMore();
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})