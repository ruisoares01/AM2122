
const firebaseConfig = {
  apiKey: "AIzaSyCbXK0WXEhR9OQb_4Kb1eeJWXzW7aN4ohU",

  authDomain: "projetoam2.firebaseapp.com",

  databaseURL: "https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app",

  projectId: "projetoam2",

  storageBucket: "projetoam2.appspot.com",

  messagingSenderId: "609341250612",

  appId: "1:609341250612:web:1508e577be23453614be61",
  
  measurementId: "G-V5RJKT9YCD"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();

//get Alunos
db.collection("usuarios")
  .get()
  .then((snap) => {
    snap.docs.forEach((doc) => {
      console.log(doc.data());

      document.querySelector("#list").innerHTML += addAlunoToList(doc);

      addContactoToList(doc);
    });
  });

//get contactos
//  db.collectionGroup('contactos').get().then((snap) => {
//   snap.docs.forEach((doc) => {
//     //console.log(doc.data());
//   });
// });

//get contactos de aluno
// db.collection("Alunos/Cewntnb5JCxUc4kXi5iz/contactos")
//   .get()
//   .then((snap) => {
//     snap.docs.forEach((doc) => {
//       //console.log(doc.id);
//       //console.log(doc.data());

//     });
//   });



db.collection("usuarios")
  .where("curso", "==", "LEI")
  .orderBy("nome")
  .limit(2)
  .get()
  .then((snap) => {
    snap.forEach((doc) => {
      console.log(doc.id, " => ", doc.data());
    });
  });

