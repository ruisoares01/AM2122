//Preencher a lista de alunos

let teste = (a, b) => {
  return a + b;
} 

function addAlunoToList(doc) {
  let aluno = doc.data();
  let html = "";

  html += "<li><a>" + " - " + aluno.email;
  html += "<li><a>" + aluno.naluno + " - " + aluno.nome;
  html += "<li><a>" + aluno.morada;
  html +=
    '<button id="' +
    doc.id +
    'Edit" class="edit" type="button" onClick="paraEdicao(this.id)"><i class="fa fa-edit"></i></button>';
  html +=
    '<button id="' +
    doc.id +
    'Rem" class="rem" type="button" onClick="removeAluno(this.id)"><i class="fa fa-trash"></i></button>';
  html += '<p class = "descPar"> Curso: ' + aluno.curso + "</p>";
  html += '<p id="' + doc.id + 'Contacto" class = "descPar"></p>';

  html += "</a></li>";
  html += "<hr>";

  alunoForm.reset();
  document.querySelector("#addAluno").value = "new";

  return html;
}

//adicionar contactos
function addContactoToList(doc) {
  let html = "";
  let texto = "";

  let docRef = db.collection("usuarios").doc(doc.id).collection("usuarios");

  //db.collection("Alunos/" + doc.id + "/contactos") ou
  docRef.get().then((snap) => {
    snap.docs.forEach((d) => {
      if (d.data().type == "mobile") {
        texto = "Telemóvel: ";
      } else {
        texto = "Email: ";
      }
      //console.log(doc.id+"Contacto");
      document.getElementById(doc.id + "Contacto").innerHTML +=
        texto + d.data().value + "<br>";
    });
  });

  //return html;
}

let form = document.querySelector("#alunoForm");
//adicionar um novo documento de alunos
form.addEventListener("submit", (e) => {
  e.preventDefault();
  let button = document.querySelector("#addAluno");

  if (button.value == "new") {
    addAluno();
  } else {
    console.log(form.value);
    editAluno(form.value);
    document.querySelector("#addAluno").value = "new";
  }

  alunoForm.reset();
});

//adicionar um aluno
function addAluno() {

  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  firebase.auth().createUserWithEmailAndPassword(email, password)
  .then((userCredential) => {

    var user = userCredential.user;
    console.log("ID: ", user.uid);

    let uniqueid;
})

  console.log("antes da collection");
  db.collection("usuarios")
    .add({
      curso: form.curse.value,
      email: form.email.value,
      linkfoto: "https://firebasestorage.googleapis.com/v0/b/projetoam2.appspot.com/o/imagens%2F0DE7C620-F4D3-4F67-9F1D-CCBEAFD22367.jpeg?alt=media&token=a04792bd-cd8f-4117-ae90-81bddcd024fd",      
      morada: form.morada.value,
      naluno: form.number.value,
      nome: form.name.value,
      online: false,
      password: form.password.value,
    })
    .then((docRef) => {
      console.log("entrou");
      uniqueid = docRef.id;
      console.log("Document written with ID: ", docRef.id);
    /*  array.forEach((c) => {
        db.collection("usuarios").add({
          type: c.type,
          value: c.value,
        });
      });*/
      console.log("Document successfully added!");
    })
  
  .catch((error) => {
    var errorCode = error.code;
    var errorMessage = error.message;
  });
}

//editar um aluno - carrega formulário com dados
function editAluno(id) {
  let docRef;
  let i = 0;
  const array = [
    {
      type: "email",
      value: form.email.value,
    },
    {
      type: "naluno",
      value: form.number.value,
    },
  ];
  docRef = db.collection("usuarios").doc(id);
  docRef
    .update({
      naluno: form.number.value,
      nome: form.name.value,
      curso: form.curse.value,
      email: form.email.value,
      morada: form.morada.value,
      online: false,
      password: form.password.value,
    })
    docRef.collection("usuarios").get().then((snap) => {
      snap.docs.forEach((doc) => {
        console.log(doc.data());
        docRef.collection("usuarios").doc(doc.id).update({
          type: array[i].type,
          value: array[i].value,
        })
        i++;
      });
      console.log("Document successfully saved!");
    })
    .catch((error) => {
      console.error("Error saving document: ", error);
    }); 
}

//Prepara o formulário para edição
function paraEdicao(id) {
  let docRef;
  let conRef;
  id = id.replace("Edit", "");

  docRef = db.collection("usuarios").doc(id);

  docRef.get().then((doc) => {
    form.number.value = doc.data().naluno;
    form.name.value = doc.data().nome;
    form.curse.value = doc.data().curso;
    form.email.value = doc.data().email;
    form.morada.value = doc.data().morada;
    form.password.value = doc.data().password;

    conRef = db.collection("usuarios").doc(id).collection("usuarios");
    conRef
      .get()
      .then((snap) => {
        snap.docs.forEach((subDoc) => {
          if (subDoc.data().type == "nalunos") {
            form.number.value = subDoc.data().value;
          } else {
            form.email.value = subDoc.data().value;
          }
        });
        console.log("Document successfully loaded!");
        document.querySelector("#addAluno").value = "editMode";
        console.log(document.querySelector("#addAluno").value);
        form.value = id;
      })
      .catch((error) => {
        console.error("Error loading document: ", error);
      });
  });
}

//remove um aluno
function removeAluno(id) {
  id = id.replace("Rem", "");
  docRef = db.collection("usuarios").doc(id);

  docRef
    .delete(() => {
      recursive: true;
    })
    .then(() => {
      console.log("Document successfully deleted!");
    })
    .catch((error) => {
      console.error("Error removing document: ", error);
    });
}

//login
function loginbackoffice(){

  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  firebase.auth().signInWithEmailAndPassword(email, password)
  .then((userCredential) => {
      var user = userCredential.user;
      var docRef = db.collection("usuarios").doc(user.uid);
      docRef.get().then((doc) => {
          let utilizador = doc.data();
          if (doc.exists) {
              console.log("Document data:", doc.data());
              console.log("email:", utilizador.email);
              if(utilizador.cargo == "admin"){
                  location.replace("index.html")
              }
          } else {
              console.log("No such document!");
          }
      }).catch((error) => {
          console.log("Error getting document:", error);
      });
  })
  .catch((error) => {
      var errorCode = error.code;
      var errorMessage = error.message;
      console.log(errorCode + errorMessage);
  });
}

//logout
function logout(){
  firebase.auth().signOut();
}

//funcao para ver a pass ao clicar no olho
let btn = document.querySelector('.lnr-eye');
btn.addEventListener('click', function() {
    let input = document.querySelector('#password');
    if(input.getAttribute('type') == 'password') {
        input.setAttribute('type', 'text');
    } else {
        input.setAttribute('type', 'password');
    }
});
