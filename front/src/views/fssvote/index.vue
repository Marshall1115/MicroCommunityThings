<template>
  <div class="login-container">
    <el-form :model="temp" ref="tempForm" class="login-form" label-position ="top"
        style="width: 80%;">
      <div class="title-container">
        <h3 class="title">中铁滨湖名邸更换物业业主调查</h3>
      </div>
      <div class="voteRule">
        <div>1，为改善小区居住环境，提高生活质量，请您加入我们。</div>
        <div>2，更换物业公司将从全国排名前十以内由业主表决筛选，进入前三将进行公开招投标。</div>
        <div>3，愿意更换物业的业主，请在下表填写支持同意，每天会汇总好具体数据提前联系上门收资料（提供房产证、身份证复印件）并签字。</div>
      </div>
        <el-form-item >
          <span slot="label">1，针对盛全物业服务不好我们要求召开业主大会、更换物业你是否愿意参加，并提交资料？</span>
            <el-radio v-model="temp.title1" label="是">愿意参加并提交资料</el-radio>
            <el-radio v-model="temp.title1" label="否">不愿意</el-radio>
        </el-form-item>
         <el-form-item label="2，请选择您所在的楼栋">
  
            <el-select v-model="temp.building" placeholder="请选择">
              <el-option
                v-for="item in boptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
        </el-form-item>
         <el-form-item label="3，请填写您的房号">
          <el-input v-model="temp.room" placeholder="请输入您的房间号" />
        </el-form-item>
         <el-form-item label="4，请填写您的姓名">
          <el-input v-model="temp.name" placeholder="请输入您的姓名" />
        </el-form-item>
         <el-form-item label="5，请填写您的联系号码">
          <el-input v-model="temp.tel" placeholder="请输入手机号" />
        </el-form-item>
      <el-button  :loading="loading"  type="primary" style="width:40%;margin-bottom:30px;" @click.native.prevent="saveForm">保存</el-button>

    </el-form>
  </div>
</template>

<script>
import {
  saveFssVote
} from "@/api/user";
import { validUsername } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    return {
      temp: {
        title1: '',
        title2: '',
        building: '',
        room: '',
        tel: '',
        name: ''
      },
       boptions: [{
          value: '1',
          label: '1#'
        }, {
          value: '2',
          label: '2#'
        }, {
           value: '3',
          label: '3#'
        }, {
           value: '4',
          label: '4#'
        }, {
          value: '5',
          label: '5#'
        }, {
          value: '6',
          label: '6#'
        }, {
          value: '7',
          label: '7#'
        }, {
          value: '8',
          label: '8#'
        }, {
          value: '9',
          label: '9#'
        }, {
          value: '10',
          label: '10#'
        }, {
          value: '11',
          label: '11#'
        }, {
          value: '12',
          label: '12#'
        }, {
          value: '13',
          label: '13#'
        }, {
          value: '14',
          label: '14#'
        }, {
          value: '15',
          label: '15#'
        }, {
          value: '16',
          label: '16#'
        }, {
          value: '17',
          label: '17#'
        }, {
          value: '18',
          label: '18#'
        }, {
          value: '19',
          label: '19#'
        }
        ],
      loading: false
    }
  },

  methods: {
    saveForm() {
          this.loading = true
          saveFssVote(this.temp).then(response => {
            var msg = "";
            if(response.code === 0){
              msg = "结果保存成功，感谢您的参与。";
            }else{
              msg = "结果保存失败，请联系管理员。";
            }
            this.$message({
              type: "success",
              message: msg
            });
            this.loading = false
            this.temp = {
              title1: '',
              title2: '',
              building: '',
              room: '',
              tel: '',
              name: ''
            }
      });
    }
  }
}
</script>


<style lang="scss" scoped>
$bg:#fff;
$dark_gray:#000;
$light_gray:#000;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 60px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .title-container {
    position: relative;
    .title {
      font-size: 20px;
      color: $light_gray;
      margin: 0px auto 20px auto;
      text-align: center;
      font-weight: bold;
    }
  }
  .voteRule{
    border: 1px solid #e71e19;
    font-size: 14px;
    padding: 0 10px 10px;
    text-align: left;
    display: block;
    line-height: 24px;
    margin-bottom: 40px;
    }
}
</style>
