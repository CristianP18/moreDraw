<template>
    <spk-layout
        :routes="routesData"
        :user-info="user"
        :tenant="tenant"
        show-user
        show-filter
        user-config-btn
        @logout="logout()"
        @change-language="changeLanguage()"
    >
        <template #toolbar-slot>
            <!-- Logo positioned inside the toolbar, aligned to the left -->
            <div style="display: flex; align-items: center;">
                <!-- <div style="font-family: 'BlowBrush', sans-serif; color: #FF4500; font-weight: bold; margin-right: auto; font-size: 30px;">
                    Suporte traceBOX
                </div> -->
                <q-field
                    dense
                    outlined
                    readonly
                    class="q-mr-md"
                >
                    <div style="display: flex; align-items: center; font-family: 'BlowBrush', sans-serif; color: #FF4500; font-weight: bold; margin-right: auto; font-size: 30px;">
                        {{ $t('clientReport.supportTraceBOX') }}
                    </div>
                    <!-- <template #control>
                        {{ userName }}
                    </template> -->
                </q-field>
            </div>
        </template>
    </spk-layout>
</template>

<script>
import routes from '../router/routes';

export default {
    name: 'MyLayout',
    data() {
        return {
            routes,
            user: {
                name: '',
                companyName: 'Sparkag',
                id: null,
                picture: localStorage.getItem('picture'),
            },
            tenant: {
                name: 'SPARK AG',
                taxId: '20990205000103',
            },
        };
    },
    computed: {
        mode() {
            return this.$q.dark.isActive;
        },
        routesData() {
            return this.routes.filter((route) => route.name === 'layout')[0].children;
        },
        userName() {
            return localStorage.getItem('nameUser') || 'Visitante';
        },
        picture() {
            return localStorage.getItem('picture') || 'picture';
        },
    },
    watch: {
        userName(newName) {
            this.user.name = newName;
        },
        picture(newPic) {
            this.user.picUser = newPic;
        },
    },
    mounted() {
        this.user.name = this.userName;
    },
    methods: {
        logout() {
            localStorage.removeItem('jwt');
            localStorage.removeItem('federalTax');
            localStorage.removeItem('nameUser');
            localStorage.removeItem('picture');
            this.$router.push('/login');
        },
        changeLanguage() {
            this.$i18n.locale = localStorage.getItem('SpkLanguage');
            this.$router.go();
        },
    },
};
</script>
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=BlowBrush&display=swap');
</style>
